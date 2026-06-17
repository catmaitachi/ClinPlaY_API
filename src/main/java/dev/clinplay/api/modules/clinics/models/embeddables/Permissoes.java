package dev.clinplay.api.modules.clinics.models.embeddables;

import jakarta.persistence.Embeddable;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@AllArgsConstructor
@NoArgsConstructor
@Embeddable
@Builder
@Getter
@Setter
public class Permissoes {
    
    private boolean dono;
    private boolean adminExercicios;
    private boolean adminPacientes;
    private boolean adminProfissionais;
    private boolean adminClinica;

}
