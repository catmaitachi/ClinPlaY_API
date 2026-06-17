package dev.clinplay.api.modules.clinics.dtos;

import java.time.LocalDate;
import java.util.UUID;

import dev.clinplay.api.modules.accounts.models.Profissional;
import dev.clinplay.api.modules.accounts.models.embeddables.Conselho;
import dev.clinplay.api.modules.clinics.models.ClinProfissional;
import dev.clinplay.api.modules.clinics.models.embeddables.Permissoes;
import lombok.Data;

@Data
public class ObterClinProfissional {

    private UUID vinculoId;
    private UUID profissionalId;
    private String nome;
    private String email;
    private String telefone;
    private LocalDate nascimento;
    private String avatar;
    private String crefito;
    private String especialidade;
    private String conselhoNome;
    private String conselhoNumero;
    private String conselhoUf;

    // Permissões
    private boolean dono;
    private boolean adminExercicios;
    private boolean adminPacientes;
    private boolean adminProfissionais;
    private boolean adminClinica;

    public ObterClinProfissional(ClinProfissional cp) {

        this.vinculoId = cp.getId();

        Profissional p = cp.getProfissional();
        this.profissionalId = p.getId();
        this.nome = p.getNome();
        this.email = p.getEmail();
        this.telefone = p.getTelefone();
        this.nascimento = p.getNascimento();
        this.avatar = p.getAvatar();
        this.crefito = p.getCrefito();
        this.especialidade = p.getEspecialidade();

        Conselho c = p.getConselho();
        if (c != null) {
            this.conselhoNome = c.getNome();
            this.conselhoNumero = c.getNumero();
            this.conselhoUf = c.getUf();
        }

        Permissoes perm = cp.getPermissoes();
        this.dono = perm.isDono();
        this.adminExercicios = perm.isAdminExercicios();
        this.adminPacientes = perm.isAdminPacientes();
        this.adminProfissionais = perm.isAdminProfissionais();
        this.adminClinica = perm.isAdminClinica();

    }

}
