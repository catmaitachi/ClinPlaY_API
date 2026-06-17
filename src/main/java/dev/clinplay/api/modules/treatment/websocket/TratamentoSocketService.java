package dev.clinplay.api.modules.treatment.websocket;

import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import dev.clinplay.api.modules.auth.models.Sessao;
import dev.clinplay.api.modules.auth.repositories.SessaoRepository;
import dev.clinplay.api.modules.notifications.services.NotificacaoService;
import dev.clinplay.api.modules.treatment.models.Feedback;
import dev.clinplay.api.modules.treatment.models.Prescricao;
import dev.clinplay.api.modules.treatment.models.Tratamento;
import dev.clinplay.api.modules.treatment.models.embeddables.ExercicioConfig;
import dev.clinplay.api.modules.treatment.models.embeddables.LembreteConfig;
import dev.clinplay.api.modules.treatment.repositories.ExercicioRepository;
import dev.clinplay.api.modules.treatment.repositories.FeedbackRepository;
import dev.clinplay.api.modules.treatment.repositories.PrescricaoRepository;
import dev.clinplay.api.modules.treatment.repositories.TratamentoRepository;
import dev.clinplay.api.modules.treatment.websocket.dto.EventoSaida;
import dev.clinplay.api.modules.treatment.websocket.dto.FeedbackView;
import dev.clinplay.api.modules.treatment.websocket.dto.MensagemEntrada;
import dev.clinplay.api.modules.treatment.websocket.dto.PrescricaoView;
import dev.clinplay.api.modules.treatment.websocket.dto.TratamentoView;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class TratamentoSocketService {

    private final TratamentoRepository tratamentoRepo;
    private final PrescricaoRepository prescricaoRepo;
    private final FeedbackRepository feedbackRepo;
    private final ExercicioRepository exercicioRepo;
    private final SimpMessagingTemplate messaging;
    private final NotificacaoService notificacaoService;
    private final SessaoRepository sessaoRepo;

    @Transactional
    public void processar(UUID userId, Authentication auth, UUID tratamentoId, MensagemEntrada msg) {
        try {

            if (msg.getTipo() == null) throw new IllegalArgumentException("Campo 'tipo' é obrigatório");

            Tratamento tratamento = tratamentoRepo.findById(tratamentoId)
                    .orElseThrow(() -> new IllegalArgumentException("Tratamento não encontrado"));

            boolean isProfissional = auth.getAuthorities().stream()
                    .anyMatch(a -> a.getAuthority().equals("ROLE_PROFISSIONAL"));

            switch (msg.getTipo()) {
                case OBTER              -> obter(tratamento, userId, isProfissional);
                case EDITAR_TRATAMENTO  -> editarTratamento(tratamento, userId, msg);
                case ADICIONAR_PRESCRICAO -> adicionarPrescricao(tratamento, userId, msg);
                case REMOVER_PRESCRICAO -> removerPrescricao(tratamento, userId, msg);
                case EDITAR_PRESCRICAO  -> editarPrescricao(tratamento, userId, msg);
                case REORDENAR_PRESCRICOES -> reordenarPrescricoes(tratamento, userId, msg);
                case MARCAR_FEEDBACK_VISTO -> marcarFeedbackVisto(tratamento, userId, msg);
                case CRIAR_FEEDBACK     -> criarFeedback(tratamento, userId, isProfissional, msg);
            }

        } catch (Exception e) {
            enviarErro(userId, e.getMessage());
        }
    }

    // ── Handlers ───────────────────────────────────────────────────────────────

    private void obter(Tratamento t, UUID userId, boolean isProfissional) {
        validarAcesso(t, userId, isProfissional);

        LocalDate ontem = LocalDate.now().minusDays(1);
        if (t.getUltimaAcao() != null && t.getUltimaAcao().isBefore(ontem)
                && sequenciaInterrompida(t, t.getUltimaAcao())) {
            t.setSequencia(0);
            tratamentoRepo.save(t);
        }

        broadcast(t.getId(), EventoSaida.builder()
                .evento("ESTADO_ATUAL")
                .tratamento(new TratamentoView(t))
                .build());
    }

    private void editarTratamento(Tratamento t, UUID userId, MensagemEntrada msg) {
        validarProfissional(t, userId);

        if (msg.getDescricao() != null) t.setDescricao(msg.getDescricao());
        if (msg.getFim() != null) t.setFim(LocalDate.parse(msg.getFim()));
        if (msg.getLembreteConfig() != null) aplicarLembreteConfig(t, msg.getLembreteConfig());

        tratamentoRepo.save(t);

        broadcast(t.getId(), EventoSaida.builder()
                .evento("TRATAMENTO_EDITADO")
                .tratamento(new TratamentoView(t, false))
                .build());
    }

    private void adicionarPrescricao(Tratamento t, UUID userId, MensagemEntrada msg) {
        validarProfissional(t, userId);

        if (msg.getExercicioId() == null) throw new IllegalArgumentException("exercicioId é obrigatório");
        if (msg.getDisponivel() == null) throw new IllegalArgumentException("disponivel é obrigatório");
        if (msg.getCustomizacao() == null) throw new IllegalArgumentException("customizacao é obrigatório");

        var exercicio = exercicioRepo.findById(msg.getExercicioId())
                .orElseThrow(() -> new IllegalArgumentException("Exercício não encontrado"));

        if (!exercicio.getClinica().getId().equals(t.getClinica().getId()))
            throw new IllegalArgumentException("Exercício não pertence a esta clínica");

        int proximaOrdem = prescricaoRepo.countByTratamento(t);

        Prescricao p = new Prescricao();
        p.setTratamento(t);
        p.setExercicio(exercicio);
        p.setObservacao(msg.getObservacao());
        p.setObjetivo(msg.getObjetivo());
        p.setDisponivel(msg.getDisponivel());
        p.setOrdem(proximaOrdem);
        p.setCustomizacao(toExercicioConfig(msg.getCustomizacao()));

        prescricaoRepo.save(p);

        broadcast(t.getId(), EventoSaida.builder()
                .evento("PRESCRICAO_ADICIONADA")
                .prescricao(new PrescricaoView(p))
                .build());
    }

    private void removerPrescricao(Tratamento t, UUID userId, MensagemEntrada msg) {
        validarProfissional(t, userId);

        if (msg.getPrescricaoId() == null) throw new IllegalArgumentException("prescricaoId é obrigatório");

        Prescricao p = prescricaoRepo.findById(msg.getPrescricaoId())
                .orElseThrow(() -> new IllegalArgumentException("Prescrição não encontrada"));

        if (!p.getTratamento().getId().equals(t.getId()))
            throw new IllegalArgumentException("Prescrição não pertence a este tratamento");

        prescricaoRepo.delete(p);

        // Reordena as prescrições restantes para manter sequência contínua
        List<Prescricao> restantes = prescricaoRepo.findByTratamentoOrderByOrdem(t);
        for (int i = 0; i < restantes.size(); i++) restantes.get(i).setOrdem(i);
        prescricaoRepo.saveAll(restantes);

        broadcast(t.getId(), EventoSaida.builder()
                .evento("PRESCRICAO_REMOVIDA")
                .prescricaoId(msg.getPrescricaoId())
                .build());
    }

    private void editarPrescricao(Tratamento t, UUID userId, MensagemEntrada msg) {
        validarProfissional(t, userId);

        if (msg.getPrescricaoId() == null) throw new IllegalArgumentException("prescricaoId é obrigatório");

        Prescricao p = prescricaoRepo.findById(msg.getPrescricaoId())
                .orElseThrow(() -> new IllegalArgumentException("Prescrição não encontrada"));

        if (!p.getTratamento().getId().equals(t.getId()))
            throw new IllegalArgumentException("Prescrição não pertence a este tratamento");

        if (msg.getObservacao() != null) p.setObservacao(msg.getObservacao());
        if (msg.getObjetivo() != null) p.setObjetivo(msg.getObjetivo());
        if (msg.getDisponivel() != null) p.setDisponivel(msg.getDisponivel());
        if (msg.getCustomizacao() != null) p.setCustomizacao(toExercicioConfig(msg.getCustomizacao()));

        prescricaoRepo.save(p);

        broadcast(t.getId(), EventoSaida.builder()
                .evento("PRESCRICAO_EDITADA")
                .prescricao(new PrescricaoView(p))
                .build());
    }

    private void reordenarPrescricoes(Tratamento t, UUID userId, MensagemEntrada msg) {
        validarProfissional(t, userId);

        if (msg.getOrdem() == null || msg.getOrdem().isEmpty())
            throw new IllegalArgumentException("Campo 'ordem' é obrigatório");

        List<Prescricao> prescricoes = prescricaoRepo.findByTratamentoOrderByOrdem(t);

        if (msg.getOrdem().size() != prescricoes.size())
            throw new IllegalArgumentException("Lista de IDs deve conter todas as prescrições do tratamento");

        for (int i = 0; i < msg.getOrdem().size(); i++) {
            UUID id = msg.getOrdem().get(i);
            Prescricao p = prescricoes.stream()
                    .filter(x -> x.getId().equals(id))
                    .findFirst()
                    .orElseThrow(() -> new IllegalArgumentException("ID inválido na lista de ordem: " + id));
            p.setOrdem(i);
        }

        prescricaoRepo.saveAll(prescricoes);

        broadcast(t.getId(), EventoSaida.builder()
                .evento("PRESCRICOES_REORDENADAS")
                .ordem(msg.getOrdem())
                .build());
    }

    private void marcarFeedbackVisto(Tratamento t, UUID userId, MensagemEntrada msg) {
        validarProfissional(t, userId);

        if (msg.getFeedbackId() == null) throw new IllegalArgumentException("feedbackId é obrigatório");

        Feedback f = feedbackRepo.findById(msg.getFeedbackId())
                .orElseThrow(() -> new IllegalArgumentException("Feedback não encontrado"));

        if (!f.getPrescricao().getTratamento().getId().equals(t.getId()))
            throw new IllegalArgumentException("Feedback não pertence a este tratamento");

        f.setVisto(true);
        feedbackRepo.save(f);

        broadcast(t.getId(), EventoSaida.builder()
                .evento("FEEDBACK_VISTO")
                .feedbackId(msg.getFeedbackId())
                .build());
    }

    private void criarFeedback(Tratamento t, UUID userId, boolean isProfissional, MensagemEntrada msg) {
        if (isProfissional) throw new IllegalArgumentException("Apenas pacientes podem criar feedbacks");

        validarPaciente(t, userId);

        if (msg.getPrescricaoId() == null) throw new IllegalArgumentException("prescricaoId é obrigatório");
        if (msg.getAvaliacao() == null) throw new IllegalArgumentException("avaliacao é obrigatório");

        Prescricao p = prescricaoRepo.findById(msg.getPrescricaoId())
                .orElseThrow(() -> new IllegalArgumentException("Prescrição não encontrada"));

        if (!p.getTratamento().getId().equals(t.getId()))
            throw new IllegalArgumentException("Prescrição não pertence a este tratamento");

        Feedback feedback = new Feedback();
        feedback.setPrescricao(p);
        feedback.setAvaliacao(msg.getAvaliacao());
        feedback.setComentario(msg.getComentario());
        feedback.setVisto(false);
        feedbackRepo.save(feedback);

        // Atualiza sequência, ultimaAcao e progresso
        LocalDate hoje = LocalDate.now();
        LocalDate ultima = t.getUltimaAcao();

        if (ultima == null) {
            t.setSequencia(1);
        } else if (!ultima.equals(hoje)) {
            // há um intervalo entre a última ação e hoje: só reinicia se houver dia ativo perdido
            if (sequenciaInterrompida(t, ultima)) t.setSequencia(1);
            else t.setSequencia(t.getSequencia() + 1);
        }
        // ultima == hoje: sequência já contabilizada hoje, sem alteração

        t.setUltimaAcao(hoje);
        t.setProgresso(calcularProgresso(t));
        tratamentoRepo.save(t);

        agendarNotificacaoLembrete(t, p);

        broadcast(t.getId(), EventoSaida.builder()
                .evento("FEEDBACK_CRIADO")
                .tratamento(new TratamentoView(t))
                .feedback(new FeedbackView(feedback))
                .progresso(t.getProgresso())
                .sequencia(t.getSequencia())
                .ultimaAcao(t.getUltimaAcao())
                .build());
    }

    // ── Validações ─────────────────────────────────────────────────────────────

    private void validarAcesso(Tratamento t, UUID userId, boolean isProfissional) {
        if (isProfissional) validarProfissional(t, userId);
        else validarPaciente(t, userId);
    }

    private void validarProfissional(Tratamento t, UUID userId) {
        if (t.getProfissional() == null ||
                !t.getProfissional().getProfissional().getId().equals(userId))
            throw new IllegalArgumentException("Sem permissão: você não é o profissional responsável por este tratamento");
    }

    private void validarPaciente(Tratamento t, UUID userId) {
        if (!t.getPaciente().getPaciente().getId().equals(userId))
            throw new IllegalArgumentException("Sem permissão: você não é o paciente deste tratamento");
    }

    // ── Cálculo de progresso ───────────────────────────────────────────────────

    private double calcularProgresso(Tratamento t) {
        List<Prescricao> prescricoes = t.getPrescricoes();
        if (prescricoes == null || prescricoes.isEmpty()) return 0.0;

        // Sem data de alta definida, usa hoje como referência para tratamentos em curso
        LocalDate fimRef = t.getFim() != null ? t.getFim() : LocalDate.now();

        // +1 para incluir o próprio dia de início no cômputo
        long dias = ChronoUnit.DAYS.between(t.getInicio(), fimRef) + 1;
        if (dias <= 0) return 0.0;

        long totalEsperado = 0;
        for (Prescricao p : prescricoes) {
            ExercicioConfig cfg = p.getCustomizacao() != null ? p.getCustomizacao() : p.getExercicio().getConfigPadrao();
            if (cfg == null) continue;

            int cycleDays = 1 + cfg.getDiasInativo();
            long diasEfetivos = (long) Math.ceil((double) dias / cycleDays);
            totalEsperado += (long) cfg.getVezesAoDia() * diasEfetivos;
        }

        if (totalEsperado == 0) return 0.0;

        int totalFeedbacks = prescricoes.stream()
                .mapToInt(p -> p.getFeedbacks() != null ? p.getFeedbacks().size() : 0)
                .sum();

        return Math.min((double) totalFeedbacks / totalEsperado * 100.0, 100.0);
    }

    // Retorna true se há pelo menos um dia ativo não cumprido entre ultima+1 e ontem (inclusive)
    private boolean sequenciaInterrompida(Tratamento t, LocalDate ultima) {
        LocalDate ontem = LocalDate.now().minusDays(1);
        for (LocalDate d = ultima.plusDays(1); !d.isAfter(ontem); d = d.plusDays(1)) {
            if (temDiaAtivo(t, d)) return true;
        }
        return false;
    }

    // Retorna true se alguma prescrição disponível está na fase ativa do seu ciclo naquela data
    private boolean temDiaAtivo(Tratamento t, LocalDate data) {
        List<Prescricao> prescricoes = t.getPrescricoes();
        if (prescricoes == null) return false;
        long dayIndex = ChronoUnit.DAYS.between(t.getInicio(), data);
        return prescricoes.stream()
                .filter(Prescricao::isDisponivel)
                .anyMatch(p -> {
                    ExercicioConfig cfg = p.getCustomizacao() != null
                            ? p.getCustomizacao() : p.getExercicio().getConfigPadrao();
                    int cycle = cfg != null ? 1 + cfg.getDiasInativo() : 1;
                    return dayIndex % cycle == 0;
                });
    }

    // ── Notificações ──────────────────────────────────────────────────────────

    private void agendarNotificacaoLembrete(Tratamento t, Prescricao p) {
        if (p.getProximaNotificacaoId() != null) {
            notificacaoService.cancelarPush(p.getProximaNotificacaoId());
            p.setProximaNotificacaoId(null);
        }

        if (t.getLembreteConfig() == null || !t.getLembreteConfig().isExercicios()) {
            prescricaoRepo.save(p);
            return;
        }

        ExercicioConfig cfg = p.getCustomizacao() != null ? p.getCustomizacao() : p.getExercicio().getConfigPadrao();
        if (cfg == null) return;

        LocalDateTime inicioDia = LocalDate.now().atStartOfDay();
        long feedbacksHoje = feedbackRepo.countByPrescricaoAndQuandoBetween(p, inicioDia, inicioDia.plusDays(1));

        Instant quando;
        if (feedbacksHoje < cfg.getVezesAoDia()) {
            double horas = cfg.getTempoInativo() != null ? cfg.getTempoInativo() : 0.0;
            quando = Instant.now().plusSeconds((long) (horas * 3600));
        } else {
            quando = LocalDate.now().plusDays(1).atStartOfDay(ZoneId.systemDefault()).toInstant();
        }

        Optional<Sessao> sessao = sessaoRepo
                .findTopByUsuarioIdAndFcmTokenNotNullOrderByUltimoAcessoDesc(
                        t.getPaciente().getPaciente().getId());

        if (sessao.isEmpty()) return;

        String jobId = notificacaoService.agendarPush(
                sessao.get().getFcmToken(),
                "Exercício disponível!",
                p.getExercicio().getNome(),
                quando);

        p.setProximaNotificacaoId(jobId);
        prescricaoRepo.save(p);
    }

    // ── Conversores ───────────────────────────────────────────────────────────

    private ExercicioConfig toExercicioConfig(MensagemEntrada.ExercicioConfigDto dto) {
        ExercicioConfig config = new ExercicioConfig();
        if (dto.getVezesAoDia() != null) config.setVezesAoDia(dto.getVezesAoDia());
        if (dto.getSeries() != null) config.setSeries(dto.getSeries());
        if (dto.getRepeticoes() != null) config.setRepeticoes(dto.getRepeticoes());
        if (dto.getDiasInativo() != null) config.setDiasInativo(dto.getDiasInativo());
        if (dto.getAcaoPrincipal() != null) config.setAcaoPrincipal(dto.getAcaoPrincipal());
        if (dto.getAcaoSecundaria() != null) config.setAcaoSecundaria(dto.getAcaoSecundaria());
        if (dto.getTempoInativo() != null) config.setTempoInativo(dto.getTempoInativo());
        if (dto.getTempoPrincipal() != null) config.setTempoPrincipal(dto.getTempoPrincipal());
        if (dto.getTempoSecundario() != null) config.setTempoSecundario(dto.getTempoSecundario());
        if (dto.getTempoDescanso() != null) config.setTempoDescanso(dto.getTempoDescanso());
        return config;
    }

    private void aplicarLembreteConfig(Tratamento t, MensagemEntrada.LembreteConfigDto dto) {
        LembreteConfig lc = t.getLembreteConfig() != null ? t.getLembreteConfig() : new LembreteConfig();
        if (dto.getSequencia() != null) lc.setSequencia(dto.getSequencia());
        if (dto.getExercicios() != null) lc.setExercicios(dto.getExercicios());
        t.setLembreteConfig(lc);
    }

    // ── Mensageria ─────────────────────────────────────────────────────────────

    private void broadcast(UUID tratamentoId, EventoSaida evento) {
        messaging.convertAndSend("/topic/tratamento/" + tratamentoId, evento);
    }

    private void enviarErro(UUID userId, String mensagem) {
        messaging.convertAndSendToUser(userId.toString(), "/queue/erros",
                EventoSaida.builder().evento("ERRO").mensagem(mensagem).codigo(400).build());
    }

}
