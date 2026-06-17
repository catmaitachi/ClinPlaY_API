package dev.clinplay.api.modules.treatment.websocket.dto;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

import com.fasterxml.jackson.annotation.JsonInclude;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class EventoSaida {

    private String evento;

    // ESTADO_ATUAL → tratamento completo com prescrições
    private TratamentoView tratamento;

    // PRESCRICAO_ADICIONADA / PRESCRICAO_EDITADA
    private PrescricaoView prescricao;

    // PRESCRICAO_REMOVIDA
    private UUID prescricaoId;

    // PRESCRICOES_REORDENADAS
    private List<UUID> ordem;

    // FEEDBACK_CRIADO
    private FeedbackView feedback;
    private Double progresso;
    private Integer sequencia;
    private LocalDate ultimaAcao;

    // FEEDBACK_VISTO
    private UUID feedbackId;

    // ERRO → enviado ao canal /user/queue/erros do usuário
    private String mensagem;
    private Integer codigo;

}
