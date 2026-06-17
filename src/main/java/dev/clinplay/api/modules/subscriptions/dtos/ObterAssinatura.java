package dev.clinplay.api.modules.subscriptions.dtos;

import java.time.LocalDate;
import java.util.UUID;

import dev.clinplay.api.modules.subscriptions.models.Assinatura;
import dev.clinplay.api.modules.subscriptions.models.enums.StatusAssinatura;
import lombok.Data;

@Data
public class ObterAssinatura {

    private UUID id;
    private LocalDate inicio;
    private LocalDate validade;
    private StatusAssinatura status;
    private UUID clinicaId;
    private String clinicaNome;
    private UUID planoId;
    private String planoNome;

    public ObterAssinatura(Assinatura a) {

        this.id = a.getId();
        this.inicio = a.getInicio();
        this.validade = a.getValidade();
        this.status = a.getStatus();
        this.clinicaId = a.getClinica().getId();
        this.clinicaNome = a.getClinica().getNome();
        this.planoId = a.getPlano().getId();
        this.planoNome = a.getPlano().getNome();

    }

}
