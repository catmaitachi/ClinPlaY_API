package dev.clinplay.api.modules.clinics.dtos;

import lombok.Data;

@Data
public class AtualizarPermissoes {

    private boolean adminExercicios;
    private boolean adminPacientes;
    private boolean adminProfissionais;
    private boolean adminClinica;

}
