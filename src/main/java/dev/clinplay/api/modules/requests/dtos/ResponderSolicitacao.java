package dev.clinplay.api.modules.requests.dtos;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class ResponderSolicitacao {

    @NotNull
    private Boolean aprovado;

    @Size(max = 500)
    private String resposta;

}
