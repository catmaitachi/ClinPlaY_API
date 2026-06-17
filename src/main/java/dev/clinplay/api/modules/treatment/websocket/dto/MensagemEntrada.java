package dev.clinplay.api.modules.treatment.websocket.dto;

import java.util.List;
import java.util.UUID;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import lombok.Data;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class MensagemEntrada {

    private TipoMensagem tipo;

    // Referências a entidades
    private UUID prescricaoId;
    private UUID feedbackId;
    private List<UUID> ordem;

    // Campos de EDITAR_TRATAMENTO
    private String descricao;
    private String fim;
    private LembreteConfigDto lembreteConfig;

    // Campos de ADICIONAR_PRESCRICAO / EDITAR_PRESCRICAO
    private UUID exercicioId;
    private String observacao;
    private String objetivo;
    private Boolean disponivel;
    private ExercicioConfigDto customizacao;

    // Campos de CRIAR_FEEDBACK
    private Integer avaliacao;
    private String comentario;

    @Data
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class LembreteConfigDto {
        private Boolean sequencia;
        private Boolean exercicios;
    }

    @Data
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class ExercicioConfigDto {
        private Integer vezesAoDia;
        private Integer series;
        private Integer repeticoes;
        private Integer diasInativo;
        private String acaoPrincipal;
        private String acaoSecundaria;
        private Double tempoInativo;
        private Double tempoPrincipal;
        private Double tempoSecundario;
        private Double tempoDescanso;
    }

}
