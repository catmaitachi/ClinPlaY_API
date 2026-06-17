package dev.clinplay.api.modules.requests.models;

import java.time.LocalDateTime;
import java.util.UUID;

import org.springframework.data.annotation.CreatedDate;

import dev.clinplay.api.modules.clinics.models.Clinica;
import dev.clinplay.api.modules.requests.models.enums.SituacaoSolicitacao;
import dev.clinplay.api.modules.requests.models.enums.TipoSolicitacao;
import jakarta.persistence.Column;
import jakarta.persistence.DiscriminatorColumn;
import jakarta.persistence.DiscriminatorType;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Inheritance;
import jakarta.persistence.InheritanceType;
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
@Table(name = "solicitacoes")
@Inheritance(strategy = InheritanceType.JOINED)
@DiscriminatorColumn(name = "tipo", discriminatorType = DiscriminatorType.STRING)
public abstract class Solicitacao {

    @Id @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(name = "tipo", insertable = false, updatable = false) @Enumerated(EnumType.STRING)
    private TipoSolicitacao tipo;

    @Column @Enumerated(EnumType.STRING)
    private SituacaoSolicitacao situacao;

    @Column(nullable = false) @CreatedDate
    private LocalDateTime solicitadoEm;

    @Column(length = 500)
    private String mensagem;

    @Column(length = 500)
    private String resposta;

    // ↓ Relacionamentos

    @ManyToOne @JoinColumn(name = "clinica_id", nullable = false)
    private Clinica clinica;

}
