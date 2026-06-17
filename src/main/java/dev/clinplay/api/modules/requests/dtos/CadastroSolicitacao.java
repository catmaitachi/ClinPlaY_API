package dev.clinplay.api.modules.requests.dtos;

import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class CadastroSolicitacao {

    @Size(max = 500)
    private String mensagem;

}
