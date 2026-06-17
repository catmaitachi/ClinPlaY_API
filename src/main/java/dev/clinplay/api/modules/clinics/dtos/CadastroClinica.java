package dev.clinplay.api.modules.clinics.dtos;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class CadastroClinica {

    @NotBlank @Size(max = 100)
    private String nome;

    @NotBlank @Size(max = 18)
    private String cnpj;

    @NotBlank @Size(max = 25) @Pattern(regexp = "^@[a-zA-Z0-9_.]{2,24}$", message = "A tag deve começar com @ e conter apenas letras, números, _ ou .")
    private String tag;

    @NotBlank @Size(max = 100)
    private String especialidade;

    @NotBlank @Size(min = 2, max = 2)
    private String uf;

    @NotBlank @Size(max = 100)
    private String cidade;

}
