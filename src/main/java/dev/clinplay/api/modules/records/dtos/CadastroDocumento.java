package dev.clinplay.api.modules.records.dtos;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class CadastroDocumento {

    @NotBlank @Size(max = 100)
    private String nome;

    @NotBlank
    private String conteudo;

    @NotBlank @Size(max = 20)
    private String versao;

}
