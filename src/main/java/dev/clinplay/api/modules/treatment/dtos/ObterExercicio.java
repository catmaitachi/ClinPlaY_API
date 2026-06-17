package dev.clinplay.api.modules.treatment.dtos;

import java.util.UUID;

import dev.clinplay.api.modules.treatment.models.Exercicio;
import dev.clinplay.api.modules.treatment.models.embeddables.ExercicioConfig;
import dev.clinplay.api.modules.treatment.models.enums.Jogo;
import lombok.Getter;

@Getter
public class ObterExercicio {

    private UUID id;
    private String nome;
    private String descricao;
    private Jogo jogo;
    private String videoUrl;
    private ExercicioConfig configPadrao;

    public ObterExercicio(Exercicio e) {
        this.id = e.getId();
        this.nome = e.getNome();
        this.descricao = e.getDescricao();
        this.jogo = e.getJogo();
        this.videoUrl = e.getVideoUrl();
        this.configPadrao = e.getConfigPadrao();
    }

}
