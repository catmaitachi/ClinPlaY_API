package dev.clinplay.api.modules.subscriptions.dtos;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class CadastroPlano {

    @NotBlank @Size(max = 100)
    private String nome;

    @Min(0)
    private int maxProfissionais;

    @Min(0)
    private int maxPacientes;

    @Min(0)
    private int maxExercicios;

    private boolean disponivel;

}
