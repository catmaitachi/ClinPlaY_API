package dev.clinplay.api.modules.treatment.dtos;

import java.time.LocalDateTime;
import java.util.UUID;

/** Uma linha do ranking de participação: exercícios concluídos por paciente. */
public record ObterRankingPaciente(
    UUID pacienteId,
    String nome,
    String avatar,
    Long execucoes,
    LocalDateTime ultimaExecucao
) {}
