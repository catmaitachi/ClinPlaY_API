package dev.clinplay.api.modules.accounts.models;

import java.util.List;

import dev.clinplay.api.modules.accounts.models.embeddables.Conselho;
import dev.clinplay.api.modules.clinics.models.ClinProfissional;
import dev.clinplay.api.modules.requests.models.SolicitacaoProfissional;
import jakarta.persistence.AttributeOverride;
import jakarta.persistence.AttributeOverrides;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Embedded;
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
@Table(name = "profissionais")
@DiscriminatorValue("PROFISSIONAL")
public class Profissional extends Usuario {

    @Column(nullable = false, unique = true)
    private String crefito;

    @Column(nullable = false)
    private String especialidade;

    @Embedded @AttributeOverrides({
        @AttributeOverride(name = "nome",   column = @Column(name = "conselho_nome")),
        @AttributeOverride(name = "numero", column = @Column(name = "conselho_numero")),
        @AttributeOverride(name = "uf",     column = @Column(name = "conselho_uf"))
    })
    private Conselho conselho;

    // ↓ Relacionamentos

    @OneToMany(mappedBy = "profissional", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private List<SolicitacaoProfissional> solicitacoes;

    @OneToMany(mappedBy = "profissional", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private List<ClinProfissional> clinicas;
    
}
