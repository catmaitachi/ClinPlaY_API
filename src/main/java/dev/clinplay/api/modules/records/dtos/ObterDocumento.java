package dev.clinplay.api.modules.records.dtos;

import dev.clinplay.api.modules.records.models.Documento;
import lombok.Data;

@Data
public class ObterDocumento {

    private String nome;
    private String versao;
    private String conteudo;

    public ObterDocumento(Documento d) {

        this.nome = d.getNome();
        this.versao = d.getVersao();
        this.conteudo = d.getConteudo();

    }

}
