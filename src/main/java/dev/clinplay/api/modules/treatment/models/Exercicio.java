package dev.clinplay.api.modules.treatment.models;

import java.util.List;
import java.util.UUID;

import dev.clinplay.api.modules.clinics.models.Clinica;
import dev.clinplay.api.modules.treatment.models.embeddables.ExercicioConfig;
import dev.clinplay.api.modules.treatment.models.enums.Jogo;
import jakarta.persistence.AttributeOverride;
import jakarta.persistence.AttributeOverrides;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Embedded;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
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
@Table(name = "exercicios")
public class Exercicio {
    
    @Id @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY) @JoinColumn(name = "clinica_id", nullable = false)
    private Clinica clinica;

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

    // ↓ Relacionamentos

    @OneToMany(mappedBy = "exercicio", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private List<Prescricao> prescricoes;

}
