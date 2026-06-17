package dev.clinplay.api.modules.requests.models;

import dev.clinplay.api.modules.accounts.models.Profissional;
import dev.clinplay.api.modules.treatment.models.Exercicio;
import dev.clinplay.api.modules.treatment.models.embeddables.ExercicioConfig;
import dev.clinplay.api.modules.treatment.models.enums.Jogo;
import jakarta.persistence.AttributeOverride;
import jakarta.persistence.AttributeOverrides;
import jakarta.persistence.Column;
import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Embedded;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

@AllArgsConstructor
@NoArgsConstructor
@SuperBuilder
@Getter
@Setter
@Entity
@DiscriminatorValue("EXERCICIO")
public class SolicitacaoExercicio extends Solicitacao {

    @Column(nullable = false)
    private String nome;

    @Column(columnDefinition = "TEXT")
    private String descricao;

    @Column(columnDefinition = "VARCHAR(100)") @Enumerated(EnumType.STRING)
    private Jogo jogo;

    @Column(columnDefinition = "TEXT")
    private String videoUrl;

    @Embedded @AttributeOverrides({
        @AttributeOverride(name = "vezesAoDia",     column = @Column(name = "vezes_ao_dia_padrao")),
        @AttributeOverride(name = "series",         column = @Column(name = "series_padrao")),
        @AttributeOverride(name = "repeticoes",     column = @Column(name = "repeticoes_padrao")),
        @AttributeOverride(name = "diasInativo",    column = @Column(name = "dias_inativo_padrao")),
        @AttributeOverride(name = "tempoInativo",   column = @Column(name = "tempo_inativo_padrao")),
        @AttributeOverride(name = "tempoPrincipal", column = @Column(name = "tempo_principal_padrao")),
        @AttributeOverride(name = "tempoSecundario",column = @Column(name = "tempo_secundario_padrao")),
        @AttributeOverride(name = "tempoDescanso",  column = @Column(name = "tempo_descanso_padrao"))
    })
    private ExercicioConfig configPadrao;

    @ManyToOne @JoinColumn(name = "solicitante_id", nullable = false)
    private Profissional solicitante;

    @ManyToOne @JoinColumn(name = "exercicio_id")
    private Exercicio exercicio;

}
