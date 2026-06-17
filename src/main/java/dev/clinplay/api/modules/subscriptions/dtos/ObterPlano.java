package dev.clinplay.api.modules.subscriptions.dtos;

import java.util.UUID;

import dev.clinplay.api.modules.subscriptions.models.Plano;
import lombok.Data;

@Data
public class ObterPlano {

    private UUID id;
    private String nome;
    private int maxProfissionais;
    private int maxPacientes;
    private int maxExercicios;
    private boolean disponivel;

    public ObterPlano(Plano p) {

        this.id = p.getId();
        this.nome = p.getNome();
        this.maxProfissionais = p.getMaxProfissionais();
        this.maxPacientes = p.getMaxPacientes();
        this.maxExercicios = p.getMaxExercicios();
        this.disponivel = p.isDisponivel();

    }

}
