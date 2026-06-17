package dev.clinplay.api.modules.subscriptions.controllers;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import dev.clinplay.api.modules.subscriptions.dtos.CadastroPlano;
import dev.clinplay.api.modules.subscriptions.services.PlanoService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/plano")
@RequiredArgsConstructor
public class PlanoController {

    private final PlanoService service;

    @GetMapping
    public ResponseEntity<?> listar() {

        try {

            return ResponseEntity.ok(service.listar());

        } catch (Exception e) { return ResponseEntity.internalServerError().body("Não foi possível listar os planos: " + e.getMessage()); }

    }

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> cadastrar(@Valid @RequestBody CadastroPlano dto) {

        try {

            return ResponseEntity.ok(service.cadastrar(dto));

        } catch (IllegalArgumentException e) { return ResponseEntity.badRequest().body(e.getMessage()); }
        catch (Exception e) { return ResponseEntity.internalServerError().body("Não foi possível cadastrar o plano: " + e.getMessage()); }

    }

}
