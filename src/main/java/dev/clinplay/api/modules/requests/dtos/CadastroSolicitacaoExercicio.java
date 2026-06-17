package dev.clinplay.api.modules.requests.dtos;

import java.util.UUID;

import dev.clinplay.api.modules.treatment.models.embeddables.ExercicioConfig;
import dev.clinplay.api.modules.treatment.models.enums.Jogo;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class CadastroSolicitacaoExercicio {

    private UUID exercicioId;

    @NotBlank
    @Size(max = 100)
    private String nome;

    @Size(max = 2000)
    private String descricao;

    private Jogo jogo;

    @Size(max = 500)
    @Pattern(
        regexp = "^$|^(https?://)?(www\\.)?(youtube\\.com/(watch\\?v=|shorts/|embed/)|youtu\\.be/)[\\w\\-]{11}.*$",
        message = "A URL do vídeo deve ser do YouTube"
    )
    private String videoUrl;

    @Valid
    private ExercicioConfig configPadrao;

    @Size(max = 500)
    private String mensagem;

}
