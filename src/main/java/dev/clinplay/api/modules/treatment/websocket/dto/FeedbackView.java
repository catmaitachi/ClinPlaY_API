package dev.clinplay.api.modules.treatment.websocket.dto;

import java.time.LocalDateTime;
import java.util.UUID;

import dev.clinplay.api.modules.treatment.models.Feedback;
import lombok.Getter;

@Getter
public class FeedbackView {

    private final UUID id;
    private final int avaliacao;
    private final String comentario;
    private final LocalDateTime quando;
    private final boolean visto;
    private final UUID prescricaoId;

    public FeedbackView(Feedback f) {
        this.id = f.getId();
        this.avaliacao = f.getAvaliacao();
        this.comentario = f.getComentario();
        this.quando = f.getQuando();
        this.visto = f.isVisto();
        this.prescricaoId = f.getPrescricao().getId();
    }

}
