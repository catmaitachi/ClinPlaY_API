package dev.clinplay.api.modules.treatment.models.embeddables;

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
public class ExercicioConfig {
    
    private Integer vezesAoDia;
    private Integer series;
    private Integer repeticoes;
    private Integer diasInativo;
    private String acaoPrincipal;
    private String acaoSecundaria;
    private Double tempoInativo;    // Tempo em horas que o paciente deve ficar sem realizar o exercício.
    private Double tempoPrincipal;  // Tempo em segundos da ação principal do exercício.
    private Double tempoSecundario; // Tempo em segundos de uma ação secundária.
    private Double tempoDescanso;   // Tempo em segundos que o paciente deve descansar entre as séries.

}
