package dev.clinplay.api.modules.subscriptions.dtos;

import java.time.LocalDate;
import java.util.UUID;

import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class AdesaoPlano {

    @NotNull
    private UUID planoId;

    @NotNull @Future
    private LocalDate validade;

}
