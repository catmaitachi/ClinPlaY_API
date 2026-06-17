package dev.clinplay.api.modules.treatment.websocket.dto;

import java.util.List;
import java.util.UUID;

import dev.clinplay.api.modules.treatment.models.Prescricao;
import dev.clinplay.api.modules.treatment.models.embeddables.ExercicioConfig;
import dev.clinplay.api.modules.treatment.models.enums.Jogo;
import lombok.Getter;

@Getter
public class PrescricaoView {

    private final UUID id;
    private final String observacao;
    private final String objetivo;
    private final boolean disponivel;
    private final int ordem;
    private final UUID exercicioId;
    private final String exercicioNome;
    private final String exercicioDescricao;
    private final Jogo exercicioJogo;
    private final String exercicioVideoUrl;
    private final ExercicioConfig customizacao;
    private final List<FeedbackView> feedbacks;

    public PrescricaoView(Prescricao p) {
        this.id = p.getId();
        this.observacao = p.getObservacao();
        this.objetivo = p.getObjetivo();
        this.disponivel = p.isDisponivel();
        this.ordem = p.getOrdem();
        this.exercicioId = p.getExercicio().getId();
        this.exercicioNome = p.getExercicio().getNome();
        this.exercicioDescricao = p.getExercicio().getDescricao();
        this.exercicioJogo = p.getExercicio().getJogo();
        this.exercicioVideoUrl = p.getExercicio().getVideoUrl();
        this.customizacao = p.getCustomizacao() != null ? p.getCustomizacao() : p.getExercicio().getConfigPadrao();
        this.feedbacks = p.getFeedbacks() != null
                ? p.getFeedbacks().stream().map(FeedbackView::new).toList()
                : List.of();
    }

}
