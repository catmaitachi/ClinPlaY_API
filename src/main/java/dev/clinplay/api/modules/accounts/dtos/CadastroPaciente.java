package dev.clinplay.api.modules.accounts.dtos;

import java.time.LocalDate;
// import java.util.UUID;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Past;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class CadastroPaciente {
    
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

    @NotBlank @Size(max = 20)
    private String cpf;

    // @NotNull
    // private UUID termosDeUso;

    // @NotNull
    // private UUID politicaDePrivacidade;

}
