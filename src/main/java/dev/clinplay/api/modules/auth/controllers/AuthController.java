package dev.clinplay.api.modules.auth.controllers;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import dev.clinplay.api.modules.accounts.models.embeddables.Origem;
import dev.clinplay.api.modules.auth.services.SessaoService;
import dev.clinplay.api.modules.security.jwt.JwtService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;

import java.util.Map;
import java.util.UUID;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;


@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {

    private final JwtService jwtService;
    private final SessaoService sessaoService;
    
    @GetMapping("/setup")
    public ResponseEntity<?> setup(@RequestHeader(name = "Authorization", required = false) String authHeader) {

        try {

            String token = jwtService.extrairBearer(authHeader);

            if ( token == null || !jwtService.isSetupToken(token) || !jwtService.ehValido(token) ) return ResponseEntity.badRequest().body("Token de setup inválido ou expirado.");

            return ResponseEntity.ok(jwtService.extrairSetup(token));

        } catch (Exception e) { return ResponseEntity.internalServerError().body("Não foi possível extrair as informações de setup: " + e.getMessage()); }

    }

    @GetMapping("/refresh")
    public ResponseEntity<?> refresh(@RequestHeader(name = "Authorization", required = false) String authHeader, HttpServletRequest request) {

        try {

            String token = jwtService.extrairBearer(authHeader);

            if ( token == null || !jwtService.isRefreshToken(token) || !jwtService.ehValido(token) ) return ResponseEntity.badRequest().body("Token de refresh inválido ou expirado.");

            Map<String, String> tokens = sessaoService.refresh(token, new Origem(request));

            return ResponseEntity.ok(tokens);

        } catch (Exception e) { return ResponseEntity.internalServerError().body("Não foi possível realizar o refresh do token: " + e.getMessage()); }

    }

    @PatchMapping("/fcm-token")
    public ResponseEntity<?> atualizarFcmToken(
        @RequestHeader("Authorization") String authHeader,
        @RequestBody Map<String, String> body
    ) {
        try {
            if (!authHeader.startsWith("Bearer ")) return ResponseEntity.badRequest().body("Token de acesso não fornecido.");
            UUID sessaoId = jwtService.extrairSessao(authHeader.substring(7));
            sessaoService.atualizarFcmToken(sessaoId, body.get("fcmToken"));
            return ResponseEntity.noContent().build();
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body("Não foi possível atualizar o FCM token: " + e.getMessage());
        }
    }

    @DeleteMapping("/logout")
    public ResponseEntity<?> logout(@RequestHeader(name = "Authorization", required = false) String authHeader) {

        try {

            String token = jwtService.extrairBearer(authHeader);

            if (token == null) return ResponseEntity.badRequest().body("Token de acesso não fornecido.");

            sessaoService.encerrar(jwtService.extrairSessao(token));

            return ResponseEntity.noContent().build();

        } catch (Exception e) { return ResponseEntity.internalServerError().body("Não foi possível encerrar a sessão: " + e.getMessage()); }

    }

}
