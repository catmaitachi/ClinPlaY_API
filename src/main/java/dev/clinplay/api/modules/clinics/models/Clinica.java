package dev.clinplay.api.modules.clinics.models;

import java.util.List;
import java.util.UUID;

import dev.clinplay.api.modules.requests.models.Solicitacao;
import dev.clinplay.api.modules.subscriptions.models.Assinatura;
import dev.clinplay.api.modules.treatment.models.Exercicio;
import dev.clinplay.api.modules.treatment.models.Tratamento;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
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
@Table(name = "clinicas")
public class Clinica {

    @Id @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(nullable = false)
    private String nome;

    @Column(nullable = false, unique = true)
    private String cnpj;

    @Column(nullable = false, unique = true)
    private String tag;

    @Column(nullable = false)
    private String especialidade;

    @Column(nullable = false)
    private String uf;

    @Column(nullable = false)
    private String cidade;

    @Column
    private boolean ativo;

    // ↓ Relacionamentos

    @OneToOne(mappedBy = "clinica", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private Assinatura assinatura;

    @OneToMany(mappedBy = "clinica", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private List<ClinProfissional> profissionais;

    @OneToMany(mappedBy = "clinica", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private List<ClinPaciente> pacientes;

    @OneToMany(mappedBy = "clinica", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private List<Solicitacao> solicitacoes;

    @OneToMany(mappedBy = "clinica", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private List<Tratamento> tratamentos;

    @OneToMany(mappedBy = "clinica", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private List<Exercicio> exercicios;

}
