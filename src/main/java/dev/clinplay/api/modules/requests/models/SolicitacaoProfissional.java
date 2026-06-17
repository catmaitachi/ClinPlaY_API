package dev.clinplay.api.modules.requests.models;

import dev.clinplay.api.modules.accounts.models.Profissional;
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
@DiscriminatorValue("PROFISSIONAL")
public class SolicitacaoProfissional extends Solicitacao {

    @ManyToOne @JoinColumn(name = "profissional_id", nullable = false)
    private Profissional profissional;

}
