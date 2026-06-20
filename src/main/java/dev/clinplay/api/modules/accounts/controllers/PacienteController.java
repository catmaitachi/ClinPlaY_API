package dev.clinplay.api.modules.accounts.controllers;

import java.util.Map;
import java.util.UUID;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import dev.clinplay.api.modules.accounts.dtos.AtualizarPaciente;
import dev.clinplay.api.modules.accounts.dtos.CadastroPaciente;
import dev.clinplay.api.modules.accounts.models.Paciente;
import dev.clinplay.api.modules.accounts.models.embeddables.Origem;
import dev.clinplay.api.modules.accounts.services.PacienteService;
import dev.clinplay.api.modules.auth.models.Sessao;
import dev.clinplay.api.modules.auth.services.SessaoService;
import dev.clinplay.api.modules.security.jwt.JwtService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/paciente")
@RequiredArgsConstructor
public class PacienteController {

    private final PacienteService service;
    private final SessaoService sessaoService;
    private final JwtService jwtService;

    @GetMapping
    @PreAuthorize("hasRole('PACIENTE')")
    public ResponseEntity<?> obter(@AuthenticationPrincipal UUID id) {

        try {

            return ResponseEntity.ok(service.obter(id));

        } catch (Exception e) { return ResponseEntity.internalServerError().body("Não foi possível obter os dados do paciente: " + e.getMessage()); }

    }

    @PutMapping
    @PreAuthorize("hasRole('PACIENTE')")
    public ResponseEntity<?> atualizar(@AuthenticationPrincipal UUID id, @Valid @RequestBody AtualizarPaciente dto) {

        try {

            return ResponseEntity.ok(service.atualizar(id, dto));

        } catch (IllegalArgumentException e) { return ResponseEntity.badRequest().body(e.getMessage()); }
        catch (Exception e) { return ResponseEntity.internalServerError().body("Não foi possível atualizar os dados do paciente: " + e.getMessage()); }

    }

    @DeleteMapping
    @PreAuthorize("hasRole('PACIENTE')")
    public ResponseEntity<?> inativar(@AuthenticationPrincipal UUID id) {

        try {

            service.inativar(id);
            sessaoService.encerrarTodas(id);

            return ResponseEntity.noContent().build();

        } catch (Exception e) { return ResponseEntity.internalServerError().body("Não foi possível inativar a conta: " + e.getMessage()); }

    }

    @PostMapping
    public ResponseEntity<?> cadastrar(@RequestHeader(name = "Authorization", required = false) String authHeader, @Valid @RequestBody CadastroPaciente dto, HttpServletRequest request) {

        try {

            String token = jwtService.extrairBearer(authHeader);

            if ( token == null || !jwtService.isSetupToken(token) || !jwtService.ehValido(token) ) return ResponseEntity.badRequest().body("Token de setup inválido ou expirado.");

            Paciente p = service.cadastrar(dto, jwtService.extrairSub(token));

            String refresh = jwtService.gerarRefreshToken(p);
            Sessao s = sessaoService.iniciar(p, refresh, new Origem(request));
            String access = jwtService.gerarAcessToken(s);

            return ResponseEntity.ok(Map.of("access", access, "refresh", refresh));

        } catch (Exception e) { return ResponseEntity.internalServerError().body("Não foi possível cadastrar o paciente: " + e.getMessage()); }

    }

}
