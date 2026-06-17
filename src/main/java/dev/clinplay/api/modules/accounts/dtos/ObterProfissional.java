package dev.clinplay.api.modules.accounts.dtos;

import java.time.LocalDate;

import dev.clinplay.api.modules.accounts.models.Profissional;
import lombok.Data;

@Data
public class ObterProfissional {

    private String nome;
    private String crefito;
    private String especialidade;
    private String conselhoNome;
    private String conselhoNumero;
    private String conselhoUf;
    private String telefone;
    private String email;
    private String avatar;
    private LocalDate nascimento;

    public ObterProfissional ( Profissional p ) {

        this.nome = p.getNome();
        this.crefito = p.getCrefito();
        this.especialidade = p.getEspecialidade();
        this.conselhoNome = p.getConselho().getNome();
        this.conselhoNumero = p.getConselho().getNumero();
        this.conselhoUf = p.getConselho().getUf();
        this.telefone = p.getTelefone();
        this.email = p.getEmail();
        this.avatar = p.getAvatar();
        this.nascimento = p.getNascimento();

    }

}