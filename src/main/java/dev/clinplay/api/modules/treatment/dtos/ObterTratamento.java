package dev.clinplay.api.modules.treatment.dtos;

import java.util.UUID;

import dev.clinplay.api.modules.treatment.models.Tratamento;
import lombok.Data;

@Data
public class ObterTratamento {

    private UUID id;

    private UUID clinPacienteId;
    private UUID pacienteId;
    private String pacienteNome;
    private String pacienteAvatar;

    private UUID clinProfissionalId;
    private UUID profissionalId;
    private String profissionalNome;
    private String profissionalAvatar;
    private String profissionalCrefito;

    public ObterTratamento(Tratamento t) {
        this.id = t.getId();
        this.clinPacienteId = t.getPaciente().getId();
        this.pacienteId = t.getPaciente().getPaciente().getId();
        this.pacienteNome = t.getPaciente().getPaciente().getNome();
        this.pacienteAvatar = t.getPaciente().getPaciente().getAvatar();
        if (t.getProfissional() != null) {
            this.clinProfissionalId = t.getProfissional().getId();
            this.profissionalId = t.getProfissional().getProfissional().getId();
            this.profissionalNome = t.getProfissional().getProfissional().getNome();
            this.profissionalAvatar = t.getProfissional().getProfissional().getAvatar();
            this.profissionalCrefito = t.getProfissional().getProfissional().getCrefito();
        }
    }

}
