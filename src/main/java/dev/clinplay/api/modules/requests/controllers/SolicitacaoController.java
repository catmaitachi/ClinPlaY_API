package dev.clinplay.api.modules.requests.controllers;

import java.util.UUID;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import dev.clinplay.api.modules.requests.dtos.CadastroSolicitacao;
import dev.clinplay.api.modules.requests.dtos.CadastroSolicitacaoExercicio;
import dev.clinplay.api.modules.requests.dtos.ResponderSolicitacao;
import dev.clinplay.api.modules.requests.services.SolicitacaoService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/solicitacao")
@RequiredArgsConstructor
public class SolicitacaoController {

    private final SolicitacaoService service;

    @GetMapping("/minhas/paciente")
    @PreAuthorize("hasRole('PACIENTE')")
    public ResponseEntity<?> listarMinhasPaciente(@AuthenticationPrincipal UUID id) {

        try {

            return ResponseEntity.ok(service.listarMinhasPaciente(id));

        } catch (IllegalArgumentException e) { return ResponseEntity.badRequest().body(e.getMessage()); }
        catch (Exception e) { return ResponseEntity.internalServerError().body("Não foi possível listar as solicitações: " + e.getMessage()); }

    }

    @GetMapping("/minhas/profissional")
    @PreAuthorize("hasRole('PROFISSIONAL')")
    public ResponseEntity<?> listarMinhasProfissional(@AuthenticationPrincipal UUID id) {

        try {

            return ResponseEntity.ok(service.listarMinhasProfissional(id));

        } catch (IllegalArgumentException e) { return ResponseEntity.badRequest().body(e.getMessage()); }
        catch (Exception e) { return ResponseEntity.internalServerError().body("Não foi possível listar as solicitações: " + e.getMessage()); }

    }

    @GetMapping("/minhas/exercicios")
    @PreAuthorize("hasRole('PROFISSIONAL')")
    public ResponseEntity<?> listarMinhasExercicios(@AuthenticationPrincipal UUID id) {

        try {

            return ResponseEntity.ok(service.listarMinhasExercicios(id));

        } catch (IllegalArgumentException e) { return ResponseEntity.badRequest().body(e.getMessage()); }
        catch (Exception e) { return ResponseEntity.internalServerError().body("Não foi possível listar as solicitações: " + e.getMessage()); }

    }

    @PostMapping("/exercicio/{clinicaId}")
    @PreAuthorize("hasRole('PROFISSIONAL')")
    public ResponseEntity<?> solicitarExercicio(
            @AuthenticationPrincipal UUID id,
            @PathVariable UUID clinicaId,
            @Valid @RequestBody CadastroSolicitacaoExercicio dto) {

        try {

            return ResponseEntity.ok(service.solicitarExercicio(id, clinicaId, dto));

        } catch (IllegalArgumentException e) { return ResponseEntity.badRequest().body(e.getMessage()); }
        catch (Exception e) { return ResponseEntity.internalServerError().body("Não foi possível criar a solicitação: " + e.getMessage()); }

    }

    @PostMapping("/paciente/{tag}")
    @PreAuthorize("hasRole('PACIENTE')")
    public ResponseEntity<?> solicitarPaciente(
            @AuthenticationPrincipal UUID id,
            @PathVariable String tag,
            @RequestBody(required = false) @Valid CadastroSolicitacao dto) {

        try {

            return ResponseEntity.ok(service.solicitarPaciente(id, tag, dto));

        } catch (IllegalArgumentException e) { return ResponseEntity.badRequest().body(e.getMessage()); }
        catch (Exception e) { return ResponseEntity.internalServerError().body("Não foi possível criar a solicitação: " + e.getMessage()); }

    }

    @PostMapping("/profissional/{tag}")
    @PreAuthorize("hasRole('PROFISSIONAL')")
    public ResponseEntity<?> solicitarProfissional(
            @AuthenticationPrincipal UUID id,
            @PathVariable String tag,
            @RequestBody(required = false) @Valid CadastroSolicitacao dto) {

        try {

            return ResponseEntity.ok(service.solicitarProfissional(id, tag, dto));

        } catch (IllegalArgumentException e) { return ResponseEntity.badRequest().body(e.getMessage()); }
        catch (Exception e) { return ResponseEntity.internalServerError().body("Não foi possível criar a solicitação: " + e.getMessage()); }

    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('PROFISSIONAL')")
    public ResponseEntity<?> responder(
            @AuthenticationPrincipal UUID aprovadorId,
            @PathVariable UUID id,
            @Valid @RequestBody ResponderSolicitacao dto) {

        try {

            return ResponseEntity.ok(service.responder(aprovadorId, id, dto));

        } catch (IllegalArgumentException e) { return ResponseEntity.badRequest().body(e.getMessage()); }
        catch (Exception e) { return ResponseEntity.internalServerError().body("Não foi possível responder a solicitação: " + e.getMessage()); }

    }

}
