package dev.clinplay.api.modules.clinics.models;

import java.util.List;
import java.util.UUID;

import dev.clinplay.api.modules.accounts.models.Paciente;
import dev.clinplay.api.modules.treatment.models.Tratamento;
import jakarta.persistence.CascadeType;
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
@Table(name = "clin_pacientes")
public class ClinPaciente {
    
    @Id @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    // ↓ Relacionamentos

    @ManyToOne @JoinColumn(name = "clinica_id", nullable = false)
    private Clinica clinica;
    
    @ManyToOne @JoinColumn(name = "paciente_id", nullable = false)
    private Paciente paciente;

    @OneToMany(mappedBy = "paciente", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private List<Tratamento> tratamentos;

}
