package dev.clinplay.api.modules.treatment.dtos;

import dev.clinplay.api.modules.treatment.models.embeddables.ExercicioConfig;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class EditarExercicio {

    @NotBlank
    private String nome;

    private String descricao;

    private String videoUrl;

    private ExercicioConfig configPadrao;

}
