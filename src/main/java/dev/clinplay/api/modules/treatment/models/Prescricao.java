package dev.clinplay.api.modules.treatment.models;

import java.util.List;
import java.util.UUID;

import dev.clinplay.api.modules.treatment.models.embeddables.ExercicioConfig;
import jakarta.persistence.AttributeOverride;
import jakarta.persistence.AttributeOverrides;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Embedded;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
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
@Table(name = "prescricoes")
public class Prescricao {
    
    @Id @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(columnDefinition = "TEXT")
    private String observacao;

    @Column(columnDefinition = "TEXT")
    private String objetivo;

    @Column
    private String proximaNotificacaoId;

    @Column(nullable = false)
    private boolean disponivel;

    @Column(nullable = false)
    private int ordem;

    @Embedded @AttributeOverrides({
        @AttributeOverride(name = "vezesAoDia",     column = @Column(name = "vezes_ao_dia_custom")),
        @AttributeOverride(name = "series",         column = @Column(name = "series_custom")),
        @AttributeOverride(name = "repeticoes",     column = @Column(name = "repeticoes_custom")),
        @AttributeOverride(name = "diasInativo",    column = @Column(name = "dias_inativo_custom")),
        @AttributeOverride(name = "tempoInativo",   column = @Column(name = "tempo_inativo_custom")),
        @AttributeOverride(name = "tempoPrincipal", column = @Column(name = "tempo_principal_custom")),
        @AttributeOverride(name = "tempoSecundario",column = @Column(name = "tempo_secundario_custom")),
        @AttributeOverride(name = "tempoDescanso",  column = @Column(name = "tempo_descanso_custom"))
    })
    private ExercicioConfig customizacao;

    // ↓ Relacionamentos
    
    @ManyToOne @JoinColumn(name = "exercicio_id", nullable = false)
    private Exercicio exercicio;
    
    @ManyToOne @JoinColumn(name = "tratamento_id", nullable = false)
    private Tratamento tratamento;

    @OneToMany(mappedBy = "prescricao", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private List<Feedback> feedbacks;

}
