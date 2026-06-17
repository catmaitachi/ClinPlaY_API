package dev.clinplay.api.modules.treatment.controllers;

import java.util.UUID;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import dev.clinplay.api.modules.treatment.dtos.CadastroTratamento;
import dev.clinplay.api.modules.treatment.services.TratamentoService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/tratamento")
@RequiredArgsConstructor
@PreAuthorize("hasRole('PROFISSIONAL')")
public class TratamentoController {

    private final TratamentoService service;

    @PostMapping("/{clinicaId}")
    public ResponseEntity<?> criar(
            @AuthenticationPrincipal UUID id,
            @PathVariable UUID clinicaId,
            @Valid @RequestBody CadastroTratamento dto) {

        try {

            return ResponseEntity.ok(service.criar(id, clinicaId, dto));

        } catch (IllegalArgumentException e) { return ResponseEntity.badRequest().body(e.getMessage()); }
        catch (Exception e) { return ResponseEntity.internalServerError().body("Não foi possível criar o tratamento: " + e.getMessage()); }

    }

    @PutMapping("/{id}/finalizar")
    public ResponseEntity<?> finalizar(@AuthenticationPrincipal UUID profissionalId, @PathVariable UUID id) {

        try {

            service.finalizar(profissionalId, id);
            return ResponseEntity.noContent().build();

        } catch (IllegalArgumentException e) { return ResponseEntity.badRequest().body(e.getMessage()); }
        catch (Exception e) { return ResponseEntity.internalServerError().body("Não foi possível finalizar o tratamento: " + e.getMessage()); }

    }

}
