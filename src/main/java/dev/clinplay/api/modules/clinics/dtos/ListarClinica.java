package dev.clinplay.api.modules.clinics.dtos;

import java.util.UUID;

import dev.clinplay.api.modules.clinics.models.Clinica;
import lombok.Getter;

@Getter
public class ListarClinica {

    private UUID id;
    private String nome;
    private String tag;
    private String especialidade;
    private String uf;
    private String cidade;

    public ListarClinica(Clinica clinica) {
        this.id = clinica.getId();
        this.nome = clinica.getNome();
        this.tag = clinica.getTag();
        this.especialidade = clinica.getEspecialidade();
        this.uf = clinica.getUf();
        this.cidade = clinica.getCidade();
    }

}
