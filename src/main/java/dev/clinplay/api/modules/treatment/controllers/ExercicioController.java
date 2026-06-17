package dev.clinplay.api.modules.treatment.controllers;

import java.util.UUID;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import dev.clinplay.api.modules.treatment.dtos.EditarExercicio;
import dev.clinplay.api.modules.treatment.services.ExercicioService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/exercicio")
@RequiredArgsConstructor
@PreAuthorize("hasRole('PROFISSIONAL')")
public class ExercicioController {

    private final ExercicioService service;

    @GetMapping("/{id}")
    public ResponseEntity<?> buscar(@PathVariable UUID id) {

        try {

            return ResponseEntity.ok(service.buscar(id));

        } catch (IllegalArgumentException e) { return ResponseEntity.badRequest().body(e.getMessage()); }
        catch (Exception e) { return ResponseEntity.internalServerError().body("Não foi possível buscar o exercício: " + e.getMessage()); }

    }

    @PutMapping("/{id}")
    public ResponseEntity<?> editar(
            @AuthenticationPrincipal UUID profissionalId,
            @PathVariable UUID id,
            @Valid @RequestBody EditarExercicio dto) {

        try {

            return ResponseEntity.ok(service.editar(profissionalId, id, dto));

        } catch (IllegalArgumentException e) { return ResponseEntity.badRequest().body(e.getMessage()); }
        catch (Exception e) { return ResponseEntity.internalServerError().body("Não foi possível editar o exercício: " + e.getMessage()); }

    }

}
