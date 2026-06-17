package dev.clinplay.api.modules.requests.models;

import dev.clinplay.api.modules.accounts.models.Paciente;
import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
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
@DiscriminatorValue("PACIENTE")
public class SolicitacaoPaciente extends Solicitacao {

    @ManyToOne @JoinColumn(name = "paciente_id", nullable = false)
    private Paciente paciente;

}
