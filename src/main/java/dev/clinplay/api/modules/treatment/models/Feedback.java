package dev.clinplay.api.modules.treatment.models;

import java.time.LocalDateTime;
import java.util.UUID;

import org.hibernate.annotations.CreationTimestamp;

import jakarta.persistence.Column;
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
@Table(name = "feedback")
public class Feedback {

    @Id @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(nullable = false)
    private int avaliacao;

    @Column(columnDefinition = "TEXT")
    private String comentario;

    @Column(updatable = false) @CreationTimestamp
    private LocalDateTime quando;

    @Column(nullable = false)
    private boolean visto;

    // ↓ Relacionamentos

    @ManyToOne @JoinColumn(name = "prescricao_id", nullable = false)
    private Prescricao prescricao;
    
}
