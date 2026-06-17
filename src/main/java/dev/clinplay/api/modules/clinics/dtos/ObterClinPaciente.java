package dev.clinplay.api.modules.clinics.dtos;

import java.time.LocalDate;
import java.util.UUID;

import dev.clinplay.api.modules.accounts.models.Paciente;
import dev.clinplay.api.modules.clinics.models.ClinPaciente;
import lombok.Data;

@Data
public class ObterClinPaciente {

    private UUID vinculoId;
    private UUID pacienteId;
    private String nome;
    private String email;
    private String telefone;
    private LocalDate nascimento;
    private String avatar;
    private String cpf;

    public ObterClinPaciente(ClinPaciente cp) {

        this.vinculoId = cp.getId();

        Paciente p = cp.getPaciente();
        this.pacienteId = p.getId();
        this.nome = p.getNome();
        this.email = p.getEmail();
        this.telefone = p.getTelefone();
        this.nascimento = p.getNascimento();
        this.avatar = p.getAvatar();
        this.cpf = p.getCpf();

    }

}
