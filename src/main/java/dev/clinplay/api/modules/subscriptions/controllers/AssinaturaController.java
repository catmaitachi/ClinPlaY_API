package dev.clinplay.api.modules.subscriptions.controllers;

import java.util.UUID;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import dev.clinplay.api.modules.subscriptions.dtos.AdesaoPlano;
import dev.clinplay.api.modules.subscriptions.services.AssinaturaService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/clinica")
@RequiredArgsConstructor
public class AssinaturaController {

    private final AssinaturaService service;

    @PostMapping("/{clinicaId}/plano")
    @PreAuthorize("hasRole('PROFISSIONAL')")
    public ResponseEntity<?> aderir(@AuthenticationPrincipal UUID id, @PathVariable UUID clinicaId, @Valid @RequestBody AdesaoPlano dto) {

        try {

            return ResponseEntity.ok(service.aderir(id, clinicaId, dto));

        } catch (IllegalArgumentException e) { return ResponseEntity.badRequest().body(e.getMessage()); }
        catch (Exception e) { return ResponseEntity.internalServerError().body("Não foi possível aderir ao plano: " + e.getMessage()); }

    }

    @DeleteMapping("/{clinicaId}/plano")
    @PreAuthorize("hasRole('PROFISSIONAL')")
    public ResponseEntity<?> cancelar(@AuthenticationPrincipal UUID id, @PathVariable UUID clinicaId) {

        try {

            return ResponseEntity.ok(service.cancelar(id, clinicaId));

        } catch (IllegalArgumentException e) { return ResponseEntity.badRequest().body(e.getMessage()); }
        catch (Exception e) { return ResponseEntity.internalServerError().body("Não foi possível cancelar a assinatura: " + e.getMessage()); }

    }

}
