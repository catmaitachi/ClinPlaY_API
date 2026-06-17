package dev.clinplay.api.modules.treatment.models;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

import dev.clinplay.api.modules.clinics.models.ClinPaciente;
import dev.clinplay.api.modules.clinics.models.ClinProfissional;
import dev.clinplay.api.modules.clinics.models.Clinica;
import dev.clinplay.api.modules.treatment.models.embeddables.LembreteConfig;
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
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;
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
@Table(name = "tratamentos")
public class Tratamento {
    
    @Id @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(nullable = false)
    private String descricao;

    @Column(nullable = false)
    private LocalDate inicio;

    @Column
    private LocalDate fim;

    @Column
    private Double progresso;

    @Column
    private int sequencia;

    @Column
    private LocalDate ultimaAcao;

    @Embedded @AttributeOverrides({
        @AttributeOverride(name = "sequencia", column = @Column(name = "lembrete_sequencia")),
        @AttributeOverride(name = "exercicios", column = @Column(name = "lembrete_exercicios"))
    })
    private LembreteConfig lembreteConfig;

    // ↓ Relacionamentos

    @ManyToOne @JoinColumn(name = "clinica_id", nullable = false)
    private Clinica clinica;

    @ManyToOne @JoinColumn(name = "paciente_id", nullable = false)
    private ClinPaciente paciente;

    @ManyToOne @JoinColumn(name = "profissional_id") @OnDelete(action = OnDeleteAction.SET_NULL)
    private ClinProfissional profissional;

    @OneToMany(mappedBy = "tratamento", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private List<Prescricao> prescricoes;

}
