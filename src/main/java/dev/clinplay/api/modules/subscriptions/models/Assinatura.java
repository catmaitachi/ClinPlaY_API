package dev.clinplay.api.modules.subscriptions.models;

import java.time.LocalDate;
import java.util.UUID;

import dev.clinplay.api.modules.clinics.models.Clinica;
import dev.clinplay.api.modules.subscriptions.models.enums.StatusAssinatura;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToOne;
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
@Table(name = "assinaturas")
public class Assinatura {

    @Id @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(nullable = false)
    private LocalDate inicio;

    @Column(nullable = false)
    private LocalDate validade;

    @Column(nullable = false) @Enumerated(EnumType.STRING)
    private StatusAssinatura status;

    // ↓ Relacionamentos

    @OneToOne @JoinColumn(name = "clinica_id", nullable = false)
    private Clinica clinica;

    @ManyToOne @JoinColumn(name = "plano_id", nullable = false)
    private Plano plano;

}
