package dev.clinplay.api.modules.records.controllers;

import java.util.UUID;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import dev.clinplay.api.modules.records.dtos.CadastroDocumento;
import dev.clinplay.api.modules.records.services.DocumentoService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/documento")
@RequiredArgsConstructor
public class DocumentoController {

    private final DocumentoService service;

    @GetMapping("/{id}")
    public ResponseEntity<?> obter(@PathVariable UUID id) {

        try {

            return ResponseEntity.ok(service.obter(id));

        } catch (IllegalArgumentException e) { return ResponseEntity.badRequest().body(e.getMessage()); }
        catch (Exception e) { return ResponseEntity.internalServerError().body("Não foi possível obter o documento: " + e.getMessage()); }

    }

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> cadastrar(@Valid @RequestBody CadastroDocumento dto) {

        try {

            return ResponseEntity.ok(service.cadastrar(dto));

        } catch (IllegalArgumentException e) { return ResponseEntity.badRequest().body(e.getMessage()); }
        catch (Exception e) { return ResponseEntity.internalServerError().body("Não foi possível cadastrar o documento: " + e.getMessage()); }

    }

}
