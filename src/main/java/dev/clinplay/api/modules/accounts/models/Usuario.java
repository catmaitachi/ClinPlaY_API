package dev.clinplay.api.modules.accounts.models;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

import dev.clinplay.api.modules.accounts.models.enums.Perfil;
import dev.clinplay.api.modules.auth.models.Login;
import dev.clinplay.api.modules.auth.models.Sessao;
import dev.clinplay.api.modules.records.models.Contrato;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.DiscriminatorColumn;
import jakarta.persistence.DiscriminatorType;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Inheritance;
import jakarta.persistence.InheritanceType;
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
@Table(name = "usuarios")
@Inheritance(strategy = InheritanceType.JOINED)
@DiscriminatorColumn(name = "perfil", discriminatorType = DiscriminatorType.STRING)
public abstract class Usuario {

    @Id @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(nullable = false, unique = true)
    private String email;

    @Column(nullable = false)
    private String nome;

    @Column(nullable = false)
    private String telefone;

    @Column(nullable = false)
    private LocalDate nascimento;

    @Column(columnDefinition = "TEXT")
    private String avatar;

    @Column(name = "perfil", insertable = false, updatable = false) @Enumerated(EnumType.STRING)
    private Perfil perfil;

    @Column(nullable = false)
    private boolean admin;

    @Column
    private boolean ativo;

    // ↓ Relacionamentos

    @OneToMany(mappedBy = "usuario", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private List<Login> logins;

    @OneToMany(mappedBy = "usuario", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private List<Sessao> sessoes;

    @OneToMany(mappedBy = "usuario", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private List<Contrato> contratos;

}
