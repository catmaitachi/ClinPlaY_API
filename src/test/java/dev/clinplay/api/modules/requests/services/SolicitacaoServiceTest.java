package dev.clinplay.api.modules.requests.services;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.AdditionalAnswers.returnsFirstArg;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Optional;
import java.util.UUID;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import dev.clinplay.api.modules.accounts.models.Profissional;
import dev.clinplay.api.modules.accounts.repositories.ProfissionalRepository;
import dev.clinplay.api.modules.clinics.models.ClinProfissional;
import dev.clinplay.api.modules.clinics.models.Clinica;
import dev.clinplay.api.modules.clinics.models.embeddables.Permissoes;
import dev.clinplay.api.modules.clinics.repositories.ClinProfissionalRepository;
import dev.clinplay.api.modules.clinics.repositories.ClinicaRepository;
import dev.clinplay.api.modules.requests.dtos.CadastroSolicitacaoExercicio;
import dev.clinplay.api.modules.requests.dtos.ObterSolicitacaoExercicio;
import dev.clinplay.api.modules.requests.models.enums.SituacaoSolicitacao;
import dev.clinplay.api.modules.requests.models.enums.TipoSolicitacao;
import dev.clinplay.api.modules.requests.repositories.SolicitacaoExercicioRepository;
import dev.clinplay.api.modules.requests.websocket.SolicitacaoSocketService;
import dev.clinplay.api.modules.treatment.models.Exercicio;
import dev.clinplay.api.modules.treatment.repositories.ExercicioRepository;

@ExtendWith(MockitoExtension.class)
class SolicitacaoServiceTest {

    @Mock private ClinicaRepository clinicaRepository;
    @Mock private ProfissionalRepository profissionalRepository;
    @Mock private ClinProfissionalRepository clinProfissionalRepository;
    @Mock private SolicitacaoExercicioRepository solicitacaoExercicioRepository;
    @Mock private ExercicioRepository exercicioRepository;
    @Mock private SolicitacaoSocketService socketService;

    @InjectMocks private SolicitacaoService service;

    private final UUID clinicaId = UUID.randomUUID();
    private final UUID profissionalId = UUID.randomUUID();
    private Clinica clinica;

    @BeforeEach
    void preparar() {
        clinica = Clinica.builder().id(clinicaId).nome("Clínica").build();
        Profissional profissional = Profissional.builder().id(profissionalId).nome("Fisio").build();
        // Profissional comum: sem adminExercicios, a solicitação ficaria pendente.
        ClinProfissional vinculo = ClinProfissional.builder()
            .clinica(clinica).profissional(profissional)
            .permissoes(Permissoes.builder().build())
            .build();

        when(clinicaRepository.findById(clinicaId)).thenReturn(Optional.of(clinica));
        when(profissionalRepository.findById(profissionalId)).thenReturn(Optional.of(profissional));
        when(clinProfissionalRepository.findByClinicaAndProfissional(clinica, profissional))
            .thenReturn(Optional.of(vinculo));
        when(solicitacaoExercicioRepository.save(any())).then(returnsFirstArg());
    }

    private CadastroSolicitacaoExercicio novoExercicio() {
        CadastroSolicitacaoExercicio dto = new CadastroSolicitacaoExercicio();
        dto.setNome("Ponte");
        dto.setDescricao("Elevação de quadril");
        return dto;
    }

    @Test
    void comAprovacaoAutomaticaOExercicioDeProfissionalComumEntraAprovado() {
        clinica.setAprovacaoAutomaticaExercicios(true);

        ObterSolicitacaoExercicio resultado =
            service.solicitarExercicio(profissionalId, clinicaId, novoExercicio());

        assertThat(resultado.getSituacao()).isEqualTo(SituacaoSolicitacao.APROVADA);
        verify(exercicioRepository).save(any(Exercicio.class));
        verify(socketService, never()).notificarNovaSolicitacao(any(), any(), any());
    }

    @Test
    void semAprovacaoAutomaticaOExercicioDeProfissionalComumFicaPendente() {
        ObterSolicitacaoExercicio resultado =
            service.solicitarExercicio(profissionalId, clinicaId, novoExercicio());

        assertThat(resultado.getSituacao()).isEqualTo(SituacaoSolicitacao.PENDENTE);
        verify(exercicioRepository, never()).save(any());
        verify(socketService).notificarNovaSolicitacao(eq(clinicaId), eq(TipoSolicitacao.EXERCICIO), any());
    }

}
