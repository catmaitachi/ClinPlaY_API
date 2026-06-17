package dev.clinplay.api.modules.clinics.dtos;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class EditarClinica {

    @NotBlank @Size(max = 100)
    private String nome;

    @NotBlank @Size(max = 100)
    private String especialidade;

    @NotBlank @Size(min = 2, max = 2)
    private String uf;

    @NotBlank @Size(max = 100)
    private String cidade;

}
