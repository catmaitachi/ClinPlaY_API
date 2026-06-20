package dev.clinplay.api.modules.security.jwt;

import java.nio.charset.StandardCharsets;
import java.sql.Date;
import java.time.Instant;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.function.Function;

import javax.crypto.SecretKey;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseCookie;
import org.springframework.stereotype.Service;

import dev.clinplay.api.modules.accounts.models.Usuario;
import dev.clinplay.api.modules.accounts.models.enums.Perfil;
import dev.clinplay.api.modules.auth.models.Login;
import dev.clinplay.api.modules.auth.models.Sessao;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;

@Service
public class JwtService {

    private final SecretKey secretKey;
    private final long expiracaoAcessToken;
    private final long expiracaoRefreshToken;
    private final long expiracaoSetupToken;

    public JwtService (

        @Value("${security.secret}") String secretKey,
        @Value("${jwt.access.expiration}") long expiracaoAcessToken,
        @Value("${jwt.refresh.expiration}") long expiracaoRefreshToken,
        @Value("${jwt.setup.expiration}") long expiracaoSetupToken

    ) {

        this.secretKey = Keys.hmacShaKeyFor(secretKey.getBytes(StandardCharsets.UTF_8));
        this.expiracaoAcessToken = expiracaoAcessToken;
        this.expiracaoRefreshToken = expiracaoRefreshToken;
        this.expiracaoSetupToken = expiracaoSetupToken;

    }

    private String gerarToken(UUID id, long expiration, Map<String, Object> extraClaims) {

        Instant now = Instant.now();
        
        return Jwts.builder()
            .claims(extraClaims)
            .subject(id.toString())
            .issuedAt(Date.from(now))
            .expiration(Date.from(now.plusMillis(expiration)))
            .signWith(secretKey)
            .compact();

    }

    public String gerarAcessToken(Sessao sessao) {

        return gerarToken(sessao.getUsuario().getId(), expiracaoAcessToken, Map.of(
            "role", sessao.getUsuario().getPerfil().name(),
            "admin", sessao.getUsuario().isAdmin(),
            "session", sessao.getId(),
            "type", "access"
        ));

    }

    public String gerarRefreshToken(Usuario usuario) {
        
        return gerarToken(usuario.getId(), expiracaoRefreshToken, Map.of(
            "type", "refresh"
        ));

    }

    public String gerarSetupToken(Login login, Map<String, Object> claims) {

        Map<String, Object> c = new HashMap<>(claims);
        c.put("type", "setup");

        return gerarToken(login.getId(), expiracaoSetupToken, c);
    
    }

    public UUID extrairSub(String token) {

        return UUID.fromString(getClaim(token, Claims::getSubject));

    }

    public boolean isAccessToken(String token) {

        return "access".equals(getClaim(token, c -> c.get("type", String.class)));

    }

    public boolean isRefreshToken(String token) {

        return "refresh".equals(getClaim(token, c -> c.get("type", String.class)));

    }

    public boolean isSetupToken(String token) {

        return "setup".equals(getClaim(token, c -> c.get("type", String.class)));

    }

    public Perfil extrairRole(String token) {

        return Perfil.valueOf(getClaim(token, c -> c.get("role", String.class)));

    }

    public boolean extrairAdmin(String token) {

        return Boolean.TRUE.equals(getClaim(token, c -> c.get("admin", Boolean.class)));

    }

    public long extrairExpiracao(String token) {

        return getClaim(token, Claims::getExpiration).getTime();

    }

    public UUID extrairSessao(String token) {

        return UUID.fromString(getClaim(token, c -> c.get("session", String.class)));

    }

    public Map<String, Object> extrairSetup(String token) {

        return getClaim(token, c -> Map.of( "nome", c.get("name", String.class), "email", c.get("email", String.class), "avatar", c.get("picture", String.class) ));

    }

    public boolean ehValido(String token) {
        
        try {

            parseClaims(token);
            return true;
        
        } catch (JwtException e) { return false; }

    }

    private <T> T getClaim(String token, Function<Claims, T> resolver) {

        return resolver.apply(parseClaims(token));

    }

    private Claims parseClaims(String token) {

        return Jwts.parser()
            .verifyWith(secretKey)
            .build()
            .parseSignedClaims(token)
            .getPayload();

    }

    public ResponseCookie criarCookie(String token) {

        String nome = "ClinPlay";

        return ResponseCookie.from(nome, token)
            .maxAge((extrairExpiracao(token) - System.currentTimeMillis()) / 1000)
            .sameSite("None")
            .httpOnly(true)
            .secure(true)
            .path("/")
            .build();

    }

    
}
