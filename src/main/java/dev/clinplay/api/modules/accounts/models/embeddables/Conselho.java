package dev.clinplay.api.modules.accounts.models.embeddables;

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
public class Conselho {

    private String nome;
    private String numero;
    private String uf;
    
}
