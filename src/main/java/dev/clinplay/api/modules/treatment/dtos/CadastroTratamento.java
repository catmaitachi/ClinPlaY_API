package dev.clinplay.api.modules.treatment.dtos;

import java.time.LocalDate;
import java.util.UUID;

import dev.clinplay.api.modules.treatment.models.embeddables.LembreteConfig;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class CadastroTratamento {

    @NotNull
    private UUID clinPacienteId;

    @NotBlank
    private String descricao;

    @NotNull
    private LocalDate inicio;

    private LocalDate fim;

    private LembreteConfig lembreteConfig;

}
