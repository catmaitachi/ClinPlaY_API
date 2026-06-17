package dev.clinplay.api.modules.requests.websocket;

import java.util.UUID;

import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

import dev.clinplay.api.modules.accounts.models.Profissional;
import dev.clinplay.api.modules.accounts.repositories.ProfissionalRepository;
import dev.clinplay.api.modules.clinics.models.Clinica;
import dev.clinplay.api.modules.clinics.models.ClinProfissional;
import dev.clinplay.api.modules.clinics.models.embeddables.Permissoes;
import dev.clinplay.api.modules.clinics.repositories.ClinicaRepository;
import dev.clinplay.api.modules.clinics.repositories.ClinProfissionalRepository;
import dev.clinplay.api.modules.requests.dtos.ObterSolicitacaoExercicio;
import dev.clinplay.api.modules.requests.dtos.ObterSolicitacaoPaciente;
import dev.clinplay.api.modules.requests.dtos.ObterSolicitacaoProfissional;
import dev.clinplay.api.modules.requests.models.enums.SituacaoSolicitacao;
import dev.clinplay.api.modules.requests.models.enums.TipoSolicitacao;
import dev.clinplay.api.modules.requests.repositories.SolicitacaoExercicioRepository;
import dev.clinplay.api.modules.requests.repositories.SolicitacaoPacienteRepository;
import dev.clinplay.api.modules.requests.repositories.SolicitacaoProfissionalRepository;
import dev.clinplay.api.modules.requests.websocket.dto.EventoSolicitacoes;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class SolicitacaoSocketService {

    private final SimpMessagingTemplate messaging;
    private final ClinicaRepository clinicaRepository;
    private final ProfissionalRepository profissionalRepository;
    private final ClinProfissionalRepository clinProfissionalRepository;
    private final SolicitacaoPacienteRepository solicitacaoPacienteRepository;
    private final SolicitacaoProfissionalRepository solicitacaoProfissionalRepository;
    private final SolicitacaoExercicioRepository solicitacaoExercicioRepository;

    public void enviarEstadoAtual(UUID profissionalId, UUID clinicaId) {

        Clinica clinica = clinicaRepository.findById(clinicaId)
            .orElseThrow(() -> new IllegalArgumentException("Clínica não encontrada"));

        Profissional profissional = profissionalRepository.findById(profissionalId)
            .orElseThrow(() -> new IllegalArgumentException("Profissional não encontrado"));

        ClinProfissional vinculo = clinProfissionalRepository.findByClinicaAndProfissional(clinica, profissional)
            .orElseThrow(() -> new IllegalArgumentException("Você não faz parte desta clínica"));

        Permissoes p = vinculo.getPermissoes();
        EventoSolicitacoes.EventoSolicitacoesBuilder builder = EventoSolicitacoes.builder().evento("ESTADO_ATUAL");

        if (p.isAdminPacientes())
            builder.pacientes(solicitacaoPacienteRepository.findByClinicaAndSituacao(clinica, SituacaoSolicitacao.PENDENTE)
                .stream().map(ObterSolicitacaoPaciente::new).toList());

        if (p.isAdminProfissionais())
            builder.profissionais(solicitacaoProfissionalRepository.findByClinicaAndSituacao(clinica, SituacaoSolicitacao.PENDENTE)
                .stream().map(ObterSolicitacaoProfissional::new).toList());

        if (p.isAdminExercicios())
            builder.exercicios(solicitacaoExercicioRepository.findByClinicaAndSituacao(clinica, SituacaoSolicitacao.PENDENTE)
                .stream().map(ObterSolicitacaoExercicio::new).toList());

        enviar(profissionalId, clinicaId, builder.build());

    }

    public void notificarNovaSolicitacao(UUID clinicaId, TipoSolicitacao tipo, Object dto) {

        Clinica clinica = clinicaRepository.findById(clinicaId).orElse(null);
        if (clinica == null) return;

        clinProfissionalRepository.findByClinica(clinica).stream()
            .filter(cp -> temPermissao(cp.getPermissoes(), tipo))
            .forEach(cp -> {
                EventoSolicitacoes.EventoSolicitacoesBuilder builder = EventoSolicitacoes.builder()
                    .evento("SOLICITACAO_CRIADA")
                    .tipo(tipo.name());

                switch (tipo) {
                    case PACIENTE -> builder.paciente((ObterSolicitacaoPaciente) dto);
                    case PROFISSIONAL -> builder.profissional((ObterSolicitacaoProfissional) dto);
                    case EXERCICIO -> builder.exercicio((ObterSolicitacaoExercicio) dto);
                }

                enviar(cp.getProfissional().getId(), clinicaId, builder.build());
            });

    }

    public void notificarSolicitacaoRespondida(UUID solicitacaoId, UUID clinicaId, TipoSolicitacao tipo, SituacaoSolicitacao situacao) {

        Clinica clinica = clinicaRepository.findById(clinicaId).orElse(null);
        if (clinica == null) return;

        EventoSolicitacoes evento = EventoSolicitacoes.builder()
            .evento("SOLICITACAO_RESPONDIDA")
            .tipo(tipo.name())
            .solicitacaoId(solicitacaoId)
            .situacao(situacao.name())
            .build();

        clinProfissionalRepository.findByClinica(clinica).stream()
            .filter(cp -> temPermissao(cp.getPermissoes(), tipo))
            .forEach(cp -> enviar(cp.getProfissional().getId(), clinicaId, evento));

    }

    private boolean temPermissao(Permissoes permissoes, TipoSolicitacao tipo) {
        return switch (tipo) {
            case PACIENTE -> permissoes.isAdminPacientes();
            case PROFISSIONAL -> permissoes.isAdminProfissionais();
            case EXERCICIO -> permissoes.isAdminExercicios();
        };
    }

    private void enviar(UUID profissionalId, UUID clinicaId, EventoSolicitacoes evento) {
        messaging.convertAndSendToUser(
            profissionalId.toString(),
            "/queue/clinica/" + clinicaId + "/solicitacoes",
            evento
        );
    }

}
