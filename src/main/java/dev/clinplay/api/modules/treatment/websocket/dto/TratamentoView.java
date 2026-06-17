package dev.clinplay.api.modules.treatment.websocket.dto;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

import com.fasterxml.jackson.annotation.JsonInclude;

import dev.clinplay.api.modules.treatment.models.Tratamento;
import dev.clinplay.api.modules.treatment.models.embeddables.LembreteConfig;
import lombok.Getter;

@Getter
@JsonInclude(JsonInclude.Include.NON_NULL)
public class TratamentoView {

    private final UUID id;
    private final String descricao;
    private final LocalDate inicio;
    private final LocalDate fim;
    private final Double progresso;
    private final int sequencia;
    private final LocalDate ultimaAcao;
    private final LembreteConfig lembreteConfig;
    private final List<PrescricaoView> prescricoes;

    public TratamentoView(Tratamento t) {
        this(t, true);
    }

    public TratamentoView(Tratamento t, boolean includePrescricoes) {
        this.id = t.getId();
        this.descricao = t.getDescricao();
        this.inicio = t.getInicio();
        this.fim = t.getFim();
        this.progresso = t.getProgresso();
        this.sequencia = t.getSequencia();
        this.ultimaAcao = t.getUltimaAcao();
        this.lembreteConfig = t.getLembreteConfig();
        this.prescricoes = includePrescricoes && t.getPrescricoes() != null
                ? t.getPrescricoes().stream()
                        .sorted((a, b) -> Integer.compare(a.getOrdem(), b.getOrdem()))
                        .map(PrescricaoView::new)
                        .toList()
                : null;
    }

}
