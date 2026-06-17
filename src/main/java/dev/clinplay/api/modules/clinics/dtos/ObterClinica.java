package dev.clinplay.api.modules.clinics.dtos;

import java.util.UUID;

import dev.clinplay.api.modules.clinics.models.Clinica;
import lombok.Data;

@Data
public class ObterClinica {

    private UUID id;
    private String nome;
    private String cnpj;
    private String tag;
    private String especialidade;
    private String uf;
    private String cidade;

    public ObterClinica(Clinica c) {

        this.id = c.getId();
        this.nome = c.getNome();
        this.cnpj = c.getCnpj();
        this.tag = c.getTag();
        this.especialidade = c.getEspecialidade();
        this.uf = c.getUf();
        this.cidade = c.getCidade();

    }

}
