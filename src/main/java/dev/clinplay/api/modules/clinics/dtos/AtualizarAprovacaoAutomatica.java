package dev.clinplay.api.modules.clinics.dtos;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class AtualizarAprovacaoAutomatica {

    @NotNull
    private Boolean ativo;

}
