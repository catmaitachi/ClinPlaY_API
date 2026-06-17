package dev.clinplay.api.modules.accounts.models;

import java.util.List;

import dev.clinplay.api.modules.clinics.models.ClinPaciente;
import dev.clinplay.api.modules.requests.models.SolicitacaoPaciente;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
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
@Table(name = "pacientes")
@DiscriminatorValue("PACIENTE")
public class Paciente extends Usuario {

    @Column(nullable = false, unique = true)
    private String cpf;

    // ↓ Relacionamentos

    @OneToMany(mappedBy = "paciente", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private List<SolicitacaoPaciente> solicitacoes;

    @OneToMany(mappedBy = "paciente", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private List<ClinPaciente> clinicas;

}
