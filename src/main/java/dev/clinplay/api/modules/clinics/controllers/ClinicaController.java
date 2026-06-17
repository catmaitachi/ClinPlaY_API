package dev.clinplay.api.modules.clinics.controllers;

import java.util.UUID;

import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import dev.clinplay.api.modules.clinics.dtos.AtualizarPermissoes;
import dev.clinplay.api.modules.clinics.dtos.CadastroClinica;
import dev.clinplay.api.modules.clinics.dtos.EditarClinica;
import dev.clinplay.api.modules.clinics.services.ClinicaService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/clinica")
@RequiredArgsConstructor
public class ClinicaController {

    private final ClinicaService service;

    @GetMapping
    public ResponseEntity<?> listar(
            @RequestParam(required = false) String nome,
            @RequestParam(required = false) String especialidade,
            @RequestParam(required = false) String localizacao,
            @PageableDefault(size = 50, sort = "nome") Pageable pageable) {

        try {

            return ResponseEntity.ok(service.listar(nome, especialidade, localizacao, pageable));

        } catch (Exception e) { return ResponseEntity.internalServerError().body("Não foi possível listar as clínicas: " + e.getMessage()); }

    }

    @GetMapping("/tag/{tag}")
    public ResponseEntity<?> buscarPorTag(@PathVariable String tag) {

        try {

            return ResponseEntity.ok(service.buscarPorTag(tag));

        } catch (IllegalArgumentException e) { return ResponseEntity.badRequest().body(e.getMessage()); }
        catch (Exception e) { return ResponseEntity.internalServerError().body("Não foi possível buscar a clínica: " + e.getMessage()); }

    }

    @GetMapping("/minhas")
    public ResponseEntity<?> listarMinhasClinicas(@AuthenticationPrincipal UUID id) {

        try {

            return ResponseEntity.ok(service.listarMinhasClinicas(id));

        } catch (IllegalArgumentException e) { return ResponseEntity.badRequest().body(e.getMessage()); }
        catch (Exception e) { return ResponseEntity.internalServerError().body("Não foi possível listar as clínicas: " + e.getMessage()); }

    }

    @PostMapping
    @PreAuthorize("hasRole('PROFISSIONAL')")
    public ResponseEntity<?> cadastrar(@AuthenticationPrincipal UUID id, @Valid @RequestBody CadastroClinica dto) {

        try {

            return ResponseEntity.ok(service.cadastrar(id, dto));

        } catch (IllegalArgumentException e) { return ResponseEntity.badRequest().body(e.getMessage()); }
        catch (Exception e) { return ResponseEntity.internalServerError().body("Não foi possível cadastrar a clínica: " + e.getMessage()); }

    }

    @GetMapping("/{clinicaId}/exercicios")
    @PreAuthorize("hasRole('PROFISSIONAL')")
    public ResponseEntity<?> listarExercicios(@AuthenticationPrincipal UUID id, @PathVariable UUID clinicaId) {

        try {

            return ResponseEntity.ok(service.listarExercicios(id, clinicaId));

        } catch (IllegalArgumentException e) { return ResponseEntity.badRequest().body(e.getMessage()); }
        catch (Exception e) { return ResponseEntity.internalServerError().body("Não foi possível listar os exercícios: " + e.getMessage()); }

    }

    @GetMapping("/{clinicaId}/pacientes")
    @PreAuthorize("hasRole('PROFISSIONAL')")
    public ResponseEntity<?> listarPacientes(@AuthenticationPrincipal UUID id, @PathVariable UUID clinicaId) {

        try {

            return ResponseEntity.ok(service.listarPacientes(id, clinicaId));

        } catch (IllegalArgumentException e) { return ResponseEntity.badRequest().body(e.getMessage()); }
        catch (Exception e) { return ResponseEntity.internalServerError().body("Não foi possível listar os pacientes: " + e.getMessage()); }

    }

    @GetMapping("/{clinicaId}/profissionais")
    @PreAuthorize("hasRole('PROFISSIONAL')")
    public ResponseEntity<?> listarProfissionais(@AuthenticationPrincipal UUID id, @PathVariable UUID clinicaId) {

        try {

            return ResponseEntity.ok(service.listarProfissionais(id, clinicaId));

        } catch (IllegalArgumentException e) { return ResponseEntity.badRequest().body(e.getMessage()); }
        catch (Exception e) { return ResponseEntity.internalServerError().body("Não foi possível listar os profissionais: " + e.getMessage()); }

    }

    @PutMapping("/{clinicaId}")
    @PreAuthorize("hasRole('PROFISSIONAL')")
    public ResponseEntity<?> editar(
            @AuthenticationPrincipal UUID id,
            @PathVariable UUID clinicaId,
            @Valid @RequestBody EditarClinica dto) {

        try {

            return ResponseEntity.ok(service.editar(id, clinicaId, dto));

        } catch (IllegalArgumentException e) { return ResponseEntity.badRequest().body(e.getMessage()); }
        catch (Exception e) { return ResponseEntity.internalServerError().body("Não foi possível editar a clínica: " + e.getMessage()); }

    }

    @PutMapping("/{clinicaId}/profissionais/{profissionalId}/permissoes")
    @PreAuthorize("hasRole('PROFISSIONAL')")
    public ResponseEntity<?> atualizarPermissoes(
            @AuthenticationPrincipal UUID id,
            @PathVariable UUID clinicaId,
            @PathVariable UUID profissionalId,
            @RequestBody AtualizarPermissoes dto) {

        try {

            service.atualizarPermissoes(id, clinicaId, profissionalId, dto);
            return ResponseEntity.noContent().build();

        } catch (IllegalArgumentException e) { return ResponseEntity.badRequest().body(e.getMessage()); }
        catch (Exception e) { return ResponseEntity.internalServerError().body("Não foi possível atualizar as permissões: " + e.getMessage()); }

    }

    @DeleteMapping("/{clinicaId}/exercicio/{exercicioId}")
    @PreAuthorize("hasRole('PROFISSIONAL')")
    public ResponseEntity<?> excluirExercicio(
            @AuthenticationPrincipal UUID id,
            @PathVariable UUID clinicaId,
            @PathVariable UUID exercicioId) {

        try {

            service.excluirExercicio(id, clinicaId, exercicioId);
            return ResponseEntity.noContent().build();

        } catch (IllegalArgumentException e) { return ResponseEntity.badRequest().body(e.getMessage()); }
        catch (Exception e) { return ResponseEntity.internalServerError().body("Não foi possível excluir o exercício: " + e.getMessage()); }

    }

    @DeleteMapping("/{clinicaId}/paciente/{pacienteId}")
    @PreAuthorize("hasRole('PROFISSIONAL')")
    public ResponseEntity<?> desvincularPaciente(
            @AuthenticationPrincipal UUID id,
            @PathVariable UUID clinicaId,
            @PathVariable UUID pacienteId) {

        try {

            service.desvincularPaciente(id, clinicaId, pacienteId);
            return ResponseEntity.noContent().build();

        } catch (IllegalArgumentException e) { return ResponseEntity.badRequest().body(e.getMessage()); }
        catch (Exception e) { return ResponseEntity.internalServerError().body("Não foi possível desvincular o paciente: " + e.getMessage()); }

    }

    @DeleteMapping("/{clinicaId}/profissional/{profissionalId}")
    @PreAuthorize("hasRole('PROFISSIONAL')")
    public ResponseEntity<?> desvincularProfissional(
            @AuthenticationPrincipal UUID id,
            @PathVariable UUID clinicaId,
            @PathVariable UUID profissionalId) {

        try {

            service.desvincularProfissional(id, clinicaId, profissionalId);
            return ResponseEntity.noContent().build();

        } catch (IllegalArgumentException e) { return ResponseEntity.badRequest().body(e.getMessage()); }
        catch (Exception e) { return ResponseEntity.internalServerError().body("Não foi possível desvincular o profissional: " + e.getMessage()); }

    }

}
