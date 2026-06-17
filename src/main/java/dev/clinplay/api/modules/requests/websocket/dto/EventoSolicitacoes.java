package dev.clinplay.api.modules.requests.websocket.dto;

import java.util.List;
import java.util.UUID;

import com.fasterxml.jackson.annotation.JsonInclude;

import dev.clinplay.api.modules.requests.dtos.ObterSolicitacaoExercicio;
import dev.clinplay.api.modules.requests.dtos.ObterSolicitacaoPaciente;
import dev.clinplay.api.modules.requests.dtos.ObterSolicitacaoProfissional;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class EventoSolicitacoes {

    private String evento;

    // ESTADO_ATUAL
    private List<ObterSolicitacaoPaciente> pacientes;
    private List<ObterSolicitacaoProfissional> profissionais;
    private List<ObterSolicitacaoExercicio> exercicios;

    // SOLICITACAO_CRIADA
    private String tipo;
    private ObterSolicitacaoPaciente paciente;
    private ObterSolicitacaoProfissional profissional;
    private ObterSolicitacaoExercicio exercicio;

    // SOLICITACAO_RESPONDIDA
    private UUID solicitacaoId;
    private String situacao;

}
