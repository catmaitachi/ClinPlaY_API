package dev.clinplay.api.modules.subscriptions.models;

import java.util.List;
import java.util.UUID;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
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
@Table(name = "planos")
public class Plano {

    @Id @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(nullable = false)
    private String nome;

    // ↓ Configurações

    @Column
    private int maxProfissionais;

    @Column
    private int maxPacientes;

    @Column
    private int maxExercicios;

    @Column(nullable = false)
    private boolean disponivel;

    // ToDo - valor dos planos

    // ↓ Relacionamentos

    @OneToMany(mappedBy = "plano", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private List<Assinatura> assinaturas;

}
