package dev.clinplay.api.modules.records.models;

import java.time.Instant;
import java.util.UUID;

import org.hibernate.annotations.CreationTimestamp;

import dev.clinplay.api.modules.accounts.models.Usuario;
import dev.clinplay.api.modules.accounts.models.embeddables.Origem;
import jakarta.persistence.AttributeOverride;
import jakarta.persistence.AttributeOverrides;
import jakarta.persistence.Column;
import jakarta.persistence.Embedded;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
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
@Table(name = "contratos")
public class Contrato {

    @Id @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(nullable = false, updatable = false) @CreationTimestamp
    private Instant aceitoEm;

    @Embedded @AttributeOverrides({
        @AttributeOverride(name = "ip",     column = @Column(name = "origem_ip")),
        @AttributeOverride(name = "agente", column = @Column(name = "origem_agente"))
    })
    private Origem origem;

    // ↓ Relacionamentos

    @ManyToOne @JoinColumn(name = "documento_id", nullable = false)
    private Documento documento;

    @ManyToOne @JoinColumn(name = "usuario_id", nullable = false)
    private Usuario usuario;
    
}
