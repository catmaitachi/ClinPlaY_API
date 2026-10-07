package dev.clinplay.api.modules.treatment.websocket;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import dev.clinplay.api.modules.accounts.models.Profissional;
import dev.clinplay.api.modules.clinics.models.ClinProfissional;
import dev.clinplay.api.modules.treatment.models.Exercicio;
import dev.clinplay.api.modules.treatment.models.Prescricao;
import dev.clinplay.api.modules.treatment.models.Tratamento;
import dev.clinplay.api.modules.treatment.repositories.PrescricaoRepository;
import dev.clinplay.api.modules.treatment.repositories.TratamentoRepository;
import dev.clinplay.api.modules.treatment.websocket.dto.MensagemEntrada;
import dev.clinplay.api.modules.treatment.websocket.dto.TipoMensagem;

@ExtendWith(MockitoExtension.class)
class TratamentoSocketServiceTest {

    @Mock private TratamentoRepository tratamentoRepo;
    @Mock private PrescricaoRepository prescricaoRepo;
    @Mock private SimpMessagingTemplate messaging;

    @InjectMocks private TratamentoSocketService service;

    @Test
    void editarPrescricaoGuardaAsDuasPausasSeparadas() {
        UUID profissionalId = UUID.randomUUID();
        Tratamento tratamento = Tratamento.builder()
            .id(UUID.randomUUID())
            .profissional(ClinProfissional.builder()
                .profissional(Profissional.builder().id(profissionalId).build())
                .build())
            .build();
        Prescricao prescricao = Prescricao.builder()
            .id(UUID.randomUUID())
            .tratamento(tratamento)
            .exercicio(Exercicio.builder().id(UUID.randomUUID()).nome("Ponte").build())
            .build();

        when(tratamentoRepo.findById(tratamento.getId())).thenReturn(Optional.of(tratamento));
        when(prescricaoRepo.findById(prescricao.getId())).thenReturn(Optional.of(prescricao));

        Authentication auth = mock(Authentication.class);
        doReturn(List.of(new SimpleGrantedAuthority("ROLE_PROFISSIONAL"))).when(auth).getAuthorities();

        MensagemEntrada.ExercicioConfigDto customizacao = new MensagemEntrada.ExercicioConfigDto();
        customizacao.setTempoDescanso(5.0);
        customizacao.setTempoDescansoSeries(90.0);
        MensagemEntrada msg = new MensagemEntrada();
        msg.setTipo(TipoMensagem.EDITAR_PRESCRICAO);
        msg.setPrescricaoId(prescricao.getId());
        msg.setCustomizacao(customizacao);

        service.processar(profissionalId, auth, tratamento.getId(), msg);

        ArgumentCaptor<Prescricao> salva = ArgumentCaptor.forClass(Prescricao.class);
        verify(prescricaoRepo).save(salva.capture());
        assertThat(salva.getValue().getCustomizacao().getTempoDescanso()).isEqualTo(5.0);
        assertThat(salva.getValue().getCustomizacao().getTempoDescansoSeries()).isEqualTo(90.0);
        verify(messaging).convertAndSend(any(String.class), any(Object.class));
    }

}
