package dev.clinplay.api.modules.accounts.dtos;

import java.time.LocalDate;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Past;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class AtualizarProfissional {

    @NotBlank @Size(max = 100)
    private String nome;

    @NotBlank @Size(max = 20)
    private String telefone;

    @NotNull @Past
    private LocalDate nascimento;

    @NotBlank @Email @Size(max = 100)
    private String email;

    @Size(max = 1500)
    private String avatar;

    @NotBlank @Size(max = 100)
    private String especialidade;

    @NotBlank @Size(max = 20)
    private String crefito;

    @NotBlank @Size(max = 100)
    private String conselhoNome;

    @NotBlank @Size(max = 20)
    private String conselhoNumero;

    @NotBlank @Size(min = 2, max = 2)
    private String conselhoUf;

}
