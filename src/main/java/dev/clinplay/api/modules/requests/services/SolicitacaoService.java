package dev.clinplay.api.modules.requests.services;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import dev.clinplay.api.modules.accounts.models.Paciente;
import dev.clinplay.api.modules.accounts.models.Profissional;
import dev.clinplay.api.modules.accounts.repositories.PacienteRepository;
import dev.clinplay.api.modules.accounts.repositories.ProfissionalRepository;
import dev.clinplay.api.modules.clinics.models.Clinica;
import dev.clinplay.api.modules.clinics.models.ClinPaciente;
import dev.clinplay.api.modules.clinics.models.ClinProfissional;
import dev.clinplay.api.modules.clinics.models.embeddables.Permissoes;
import dev.clinplay.api.modules.clinics.repositories.ClinicaRepository;
import dev.clinplay.api.modules.clinics.repositories.ClinPacienteRepository;
import dev.clinplay.api.modules.clinics.repositories.ClinProfissionalRepository;
import dev.clinplay.api.modules.requests.dtos.CadastroSolicitacao;
import dev.clinplay.api.modules.requests.dtos.CadastroSolicitacaoExercicio;
import dev.clinplay.api.modules.requests.dtos.ObterSolicitacaoExercicio;
import dev.clinplay.api.modules.requests.dtos.ObterSolicitacaoPaciente;
import dev.clinplay.api.modules.requests.dtos.ObterSolicitacaoProfissional;
import dev.clinplay.api.modules.requests.dtos.ResponderSolicitacao;
import dev.clinplay.api.modules.requests.models.Solicitacao;
import dev.clinplay.api.modules.requests.models.SolicitacaoExercicio;
import dev.clinplay.api.modules.requests.models.SolicitacaoPaciente;
import dev.clinplay.api.modules.requests.models.SolicitacaoProfissional;
import dev.clinplay.api.modules.requests.models.enums.SituacaoSolicitacao;
import dev.clinplay.api.modules.requests.models.enums.TipoSolicitacao;
import dev.clinplay.api.modules.requests.repositories.SolicitacaoExercicioRepository;
import dev.clinplay.api.modules.requests.websocket.SolicitacaoSocketService;
import dev.clinplay.api.modules.requests.repositories.SolicitacaoRepository;
import dev.clinplay.api.modules.requests.repositories.SolicitacaoPacienteRepository;
import dev.clinplay.api.modules.requests.repositories.SolicitacaoProfissionalRepository;
import dev.clinplay.api.modules.subscriptions.models.Assinatura;
import dev.clinplay.api.modules.subscriptions.models.enums.StatusAssinatura;
import dev.clinplay.api.modules.subscriptions.repositories.AssinaturaRepository;
import dev.clinplay.api.modules.treatment.models.Exercicio;
import dev.clinplay.api.modules.treatment.repositories.ExercicioRepository;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class SolicitacaoService {

    private final SolicitacaoRepository repository;
    private final SolicitacaoPacienteRepository solicitacaoPacienteRepository;
    private final SolicitacaoProfissionalRepository solicitacaoProfissionalRepository;
    private final SolicitacaoExercicioRepository solicitacaoExercicioRepository;
    private final ClinicaRepository clinicaRepository;
    private final PacienteRepository pacienteRepository;
    private final ProfissionalRepository profissionalRepository;
    private final ClinPacienteRepository clinPacienteRepository;
    private final ClinProfissionalRepository clinProfissionalRepository;
    private final AssinaturaRepository assinaturaRepository;
    private final ExercicioRepository exercicioRepository;
    private final SolicitacaoSocketService socketService;

    @Transactional
    public ObterSolicitacaoPaciente solicitarPaciente(UUID pacienteId, String clinicaTag, CadastroSolicitacao dto) {

        Clinica clinica = clinicaRepository.findByTag(clinicaTag)
            .orElseThrow(() -> new IllegalArgumentException("Clínica não encontrada"));

        Paciente paciente = pacienteRepository.findById(pacienteId)
            .orElseThrow(() -> new IllegalArgumentException("Paciente não encontrado"));

        if (clinPacienteRepository.existsByClinicaAndPaciente(clinica, paciente))
            throw new IllegalArgumentException("Você já faz parte desta clínica");

        if (solicitacaoPacienteRepository.existsByClinicaAndPacienteAndSituacao(clinica, paciente, SituacaoSolicitacao.PENDENTE))
            throw new IllegalArgumentException("Você já possui uma solicitação pendente nesta clínica");

        SolicitacaoPaciente s = new SolicitacaoPaciente();
        s.setClinica(clinica);
        s.setPaciente(paciente);
        s.setSituacao(SituacaoSolicitacao.PENDENTE);
        s.setSolicitadoEm(LocalDateTime.now());
        s.setMensagem(dto != null ? dto.getMensagem() : null);

        ObterSolicitacaoPaciente resultado = new ObterSolicitacaoPaciente(solicitacaoPacienteRepository.save(s));
        socketService.notificarNovaSolicitacao(clinica.getId(), TipoSolicitacao.PACIENTE, resultado);
        return resultado;

    }

    @Transactional
    public ObterSolicitacaoProfissional solicitarProfissional(UUID profissionalId, String clinicaTag, CadastroSolicitacao dto) {

        Clinica clinica = clinicaRepository.findByTag(clinicaTag)
            .orElseThrow(() -> new IllegalArgumentException("Clínica não encontrada"));

        Profissional profissional = profissionalRepository.findById(profissionalId)
            .orElseThrow(() -> new IllegalArgumentException("Profissional não encontrado"));

        if (clinProfissionalRepository.existsByClinicaAndProfissional(clinica, profissional))
            throw new IllegalArgumentException("Você já faz parte desta clínica");

        if (solicitacaoProfissionalRepository.existsByClinicaAndProfissionalAndSituacao(clinica, profissional, SituacaoSolicitacao.PENDENTE))
            throw new IllegalArgumentException("Você já possui uma solicitação pendente nesta clínica");

        SolicitacaoProfissional s = new SolicitacaoProfissional();
        s.setClinica(clinica);
        s.setProfissional(profissional);
        s.setSituacao(SituacaoSolicitacao.PENDENTE);
        s.setSolicitadoEm(LocalDateTime.now());
        s.setMensagem(dto != null ? dto.getMensagem() : null);

        ObterSolicitacaoProfissional resultado = new ObterSolicitacaoProfissional(solicitacaoProfissionalRepository.save(s));
        socketService.notificarNovaSolicitacao(clinica.getId(), TipoSolicitacao.PROFISSIONAL, resultado);
        return resultado;

    }

    @Transactional
    public ObterSolicitacaoExercicio solicitarExercicio(UUID profissionalId, UUID clinicaId, CadastroSolicitacaoExercicio dto) {

        Clinica clinica = clinicaRepository.findById(clinicaId)
            .orElseThrow(() -> new IllegalArgumentException("Clínica não encontrada"));

        Profissional profissional = profissionalRepository.findById(profissionalId)
            .orElseThrow(() -> new IllegalArgumentException("Profissional não encontrado"));

        ClinProfissional vinculo = clinProfissionalRepository.findByClinicaAndProfissional(clinica, profissional)
            .orElseThrow(() -> new IllegalArgumentException("Você não faz parte desta clínica"));

        Exercicio exercicioAlvo = null;
        if (dto.getExercicioId() != null) {
            exercicioAlvo = exercicioRepository.findById(dto.getExercicioId())
                .orElseThrow(() -> new IllegalArgumentException("Exercício não encontrado"));
            if (!exercicioAlvo.getClinica().getId().equals(clinicaId))
                throw new IllegalArgumentException("O exercício não pertence a esta clínica");
        }

        SolicitacaoExercicio s = new SolicitacaoExercicio();
        s.setClinica(clinica);
        s.setSolicitante(profissional);
        s.setSolicitadoEm(java.time.LocalDateTime.now());
        s.setMensagem(dto.getMensagem());
        s.setExercicio(exercicioAlvo);
        s.setNome(dto.getNome());
        s.setDescricao(dto.getDescricao());
        s.setJogo(dto.getJogo());
        s.setVideoUrl(dto.getVideoUrl());
        s.setConfigPadrao(dto.getConfigPadrao());

        if (vinculo.getPermissoes().isAdminExercicios()) {
            Exercicio exercicio = exercicioAlvo != null ? exercicioAlvo : new Exercicio();
            exercicio.setClinica(clinica);
            exercicio.setNome(dto.getNome());
            exercicio.setDescricao(dto.getDescricao());
            exercicio.setJogo(dto.getJogo());
            exercicio.setVideoUrl(dto.getVideoUrl());
            exercicio.setConfigPadrao(dto.getConfigPadrao());
            exercicioRepository.save(exercicio);

            s.setSituacao(SituacaoSolicitacao.APROVADA);
        } else {
            s.setSituacao(SituacaoSolicitacao.PENDENTE);
        }

        ObterSolicitacaoExercicio resultado = new ObterSolicitacaoExercicio(solicitacaoExercicioRepository.save(s));
        if (!vinculo.getPermissoes().isAdminExercicios())
            socketService.notificarNovaSolicitacao(clinicaId, TipoSolicitacao.EXERCICIO, resultado);
        return resultado;

    }

    @Transactional
    public Object responder(UUID aprovadorId, UUID solicitacaoId, ResponderSolicitacao dto) {

        Solicitacao solicitacao = validarPendente(solicitacaoId);
        Clinica clinica = solicitacao.getClinica();

        Profissional aprovador = profissionalRepository.findById(aprovadorId)
            .orElseThrow(() -> new IllegalArgumentException("Profissional não encontrado"));

        ClinProfissional vinculo = validarVinculo(clinica, aprovador);

        Object resultado;
        TipoSolicitacao tipo;

        if (solicitacao instanceof SolicitacaoPaciente sp) {
            Assinatura assinatura = validarAssinatura(clinica);
            resultado = responderPaciente(sp, vinculo, assinatura, dto);
            tipo = TipoSolicitacao.PACIENTE;
        } else if (solicitacao instanceof SolicitacaoProfissional spr) {
            Assinatura assinatura = validarAssinatura(clinica);
            resultado = responderProfissional(spr, vinculo, assinatura, dto);
            tipo = TipoSolicitacao.PROFISSIONAL;
        } else if (solicitacao instanceof SolicitacaoExercicio se) {
            resultado = responderExercicio(se, vinculo, dto);
            tipo = TipoSolicitacao.EXERCICIO;
        } else {
            throw new IllegalStateException("Tipo de solicitação desconhecido");
        }

        SituacaoSolicitacao situacao = dto.getAprovado() ? SituacaoSolicitacao.APROVADA : SituacaoSolicitacao.REJEITADA;
        socketService.notificarSolicitacaoRespondida(solicitacaoId, clinica.getId(), tipo, situacao);

        return resultado;

    }

    private Solicitacao validarPendente(UUID solicitacaoId) {

        Solicitacao s = repository.findById(solicitacaoId)
            .orElseThrow(() -> new IllegalArgumentException("Solicitação não encontrada"));

        if (s.getSituacao() != SituacaoSolicitacao.PENDENTE)
            throw new IllegalArgumentException("Apenas solicitações pendentes podem ser respondidas");

        return s;

    }

    private ClinProfissional validarVinculo(Clinica clinica, Profissional aprovador) {

        return clinProfissionalRepository
            .findByClinicaAndProfissional(clinica, aprovador)
            .orElseThrow(() -> new IllegalArgumentException("Você não faz parte desta clínica"));

    }

    private Assinatura validarAssinatura(Clinica clinica) {

        Assinatura a = assinaturaRepository.findByClinica(clinica)
            .orElseThrow(() -> new IllegalArgumentException("A clínica não possui um plano ativo"));

        if (a.getStatus() != StatusAssinatura.ATIVA)
            throw new IllegalArgumentException("A assinatura da clínica não está ativa");

        return a;

    }

    private ObterSolicitacaoPaciente responderPaciente(SolicitacaoPaciente sp, ClinProfissional vinculo, Assinatura assinatura, ResponderSolicitacao dto) {

        if (!vinculo.getPermissoes().isAdminPacientes())
            throw new IllegalArgumentException("Você não tem permissão para aprovar solicitações de pacientes");

        if (dto.getAprovado()) {
            long total = clinPacienteRepository.countByClinica(sp.getClinica());
            if (total >= assinatura.getPlano().getMaxPacientes())
                throw new IllegalArgumentException("Limite de pacientes do plano atingido (" + assinatura.getPlano().getMaxPacientes() + ")");

            ClinPaciente cp = new ClinPaciente();
            cp.setClinica(sp.getClinica());
            cp.setPaciente(sp.getPaciente());
            clinPacienteRepository.save(cp);

            sp.setSituacao(SituacaoSolicitacao.APROVADA);
            sp.setResposta(dto.getResposta());
            return new ObterSolicitacaoPaciente(solicitacaoPacienteRepository.save(sp));
        }

        sp.setSituacao(SituacaoSolicitacao.REJEITADA);
        sp.setResposta(dto.getResposta());
        return new ObterSolicitacaoPaciente(solicitacaoPacienteRepository.save(sp));

    }

    private ObterSolicitacaoProfissional responderProfissional(SolicitacaoProfissional spr, ClinProfissional vinculo, Assinatura assinatura, ResponderSolicitacao dto) {

        if (!vinculo.getPermissoes().isAdminProfissionais())
            throw new IllegalArgumentException("Você não tem permissão para aprovar solicitações de profissionais");

        if (dto.getAprovado()) {
            long total = clinProfissionalRepository.countByClinica(spr.getClinica());
            if (total >= assinatura.getPlano().getMaxProfissionais())
                throw new IllegalArgumentException("Limite de profissionais do plano atingido (" + assinatura.getPlano().getMaxProfissionais() + ")");

            ClinProfissional cp = new ClinProfissional();
            cp.setClinica(spr.getClinica());
            cp.setProfissional(spr.getProfissional());

            Permissoes permissoes = Permissoes.builder()
                .adminClinica(false)
                .adminExercicios(false)
                .adminPacientes(false)
                .adminProfissionais(false)
                .build();

            cp.setPermissoes(permissoes);
            clinProfissionalRepository.save(cp);

            spr.setSituacao(SituacaoSolicitacao.APROVADA);
            spr.setResposta(dto.getResposta());
            return new ObterSolicitacaoProfissional(solicitacaoProfissionalRepository.save(spr));
        }

        spr.setSituacao(SituacaoSolicitacao.REJEITADA);
        spr.setResposta(dto.getResposta());
        return new ObterSolicitacaoProfissional(solicitacaoProfissionalRepository.save(spr));

    }

    private ObterSolicitacaoExercicio responderExercicio(SolicitacaoExercicio se, ClinProfissional vinculo, ResponderSolicitacao dto) {

        if (!vinculo.getPermissoes().isAdminExercicios())
            throw new IllegalArgumentException("Você não tem permissão para aprovar solicitações de exercícios");

        if (dto.getAprovado()) {
            Exercicio exercicio = se.getExercicio() != null
                ? se.getExercicio()
                : new Exercicio();

            exercicio.setClinica(se.getClinica());
            exercicio.setNome(se.getNome());
            exercicio.setDescricao(se.getDescricao());
            exercicio.setJogo(se.getJogo());
            exercicio.setVideoUrl(se.getVideoUrl());
            exercicio.setConfigPadrao(se.getConfigPadrao());
            exercicioRepository.save(exercicio);

            se.setSituacao(SituacaoSolicitacao.APROVADA);
            se.setResposta(dto.getResposta());
            return new ObterSolicitacaoExercicio(solicitacaoExercicioRepository.save(se));
        }

        se.setSituacao(SituacaoSolicitacao.REJEITADA);
        se.setResposta(dto.getResposta());
        return new ObterSolicitacaoExercicio(solicitacaoExercicioRepository.save(se));

    }

    public List<ObterSolicitacaoPaciente> listarMinhasPaciente(UUID pacienteId) {

        Paciente paciente = pacienteRepository.findById(pacienteId)
            .orElseThrow(() -> new IllegalArgumentException("Paciente não encontrado"));

        return solicitacaoPacienteRepository.findByPaciente(paciente)
            .stream()
            .map(ObterSolicitacaoPaciente::new)
            .toList();

    }

    public List<ObterSolicitacaoProfissional> listarMinhasProfissional(UUID profissionalId) {

        Profissional profissional = profissionalRepository.findById(profissionalId)
            .orElseThrow(() -> new IllegalArgumentException("Profissional não encontrado"));

        return solicitacaoProfissionalRepository.findByProfissional(profissional)
            .stream()
            .map(ObterSolicitacaoProfissional::new)
            .toList();

    }

    public List<ObterSolicitacaoExercicio> listarMinhasExercicios(UUID profissionalId) {

        Profissional profissional = profissionalRepository.findById(profissionalId)
            .orElseThrow(() -> new IllegalArgumentException("Profissional não encontrado"));

        return solicitacaoExercicioRepository.findBySolicitante(profissional)
            .stream()
            .map(ObterSolicitacaoExercicio::new)
            .toList();

    }

}
