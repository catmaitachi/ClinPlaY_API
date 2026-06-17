package dev.clinplay.api.modules.accounts.dtos;

import java.time.LocalDate;

import dev.clinplay.api.modules.accounts.models.Paciente;
import lombok.Data;

@Data
public class ObterPaciente {

    private String nome;
    private String cpf;
    private String telefone;
    private String email;
    private String avatar;
    private LocalDate nascimento;

    public ObterPaciente ( Paciente p ) {

        this.nome = p.getNome();
        this.cpf = p.getCpf();
        this.telefone = p.getTelefone();
        this.email = p.getEmail();
        this.avatar = p.getAvatar();
        this.nascimento = p.getNascimento();

    }

}