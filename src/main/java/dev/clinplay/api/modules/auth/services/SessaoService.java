package dev.clinplay.api.modules.auth.services;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.time.Instant;
import java.util.Base64;
import java.util.Map;
import java.util.UUID;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import dev.clinplay.api.modules.accounts.models.Usuario;
import dev.clinplay.api.modules.accounts.models.embeddables.Origem;
import dev.clinplay.api.modules.auth.models.Sessao;
import dev.clinplay.api.modules.auth.repositories.SessaoRepository;
import dev.clinplay.api.modules.security.jwt.JwtService;
import org.springframework.transaction.annotation.Transactional;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class SessaoService {
    
    private final SessaoRepository repository;
    private final PasswordEncoder encoder;
    private final JwtService jwtService;

    @Transactional
    public Sessao iniciar( Usuario usuario, String token, Origem origem ) {

        Sessao s = repository.findByUsuarioIdAndOrigem(usuario.getId(), origem).orElseGet(() -> {

                Sessao nova = Sessao.builder()
                    .usuario(usuario)
                    .refreshTokenHash(encoder.encode(digest(token)))
                    .ultimoAcesso(Instant.now())
                    .origem(origem)
                    .build();

                return repository.save(nova);

        });

        s.setRefreshTokenHash(encoder.encode(digest(token)));
        s.setUltimoAcesso(Instant.now());

        return repository.save(s);

    }

    /**
     * A sessão é achada pelo próprio refresh token, não pela origem: no celular
     * o IP muda a cada troca de rede, e buscar por IP deslogava o PWA reaberto.
     */
    @Transactional
    public Map<String, String> refresh( String token ) {

        String hash = digest(token);

        // A sessão em uso costuma ser a mais recente: testá-la primeiro poupa
        // comparações BCrypt, que são lentas de propósito.
        Sessao s = repository.findAllByUsuarioIdOrderByUltimoAcessoDesc(jwtService.extrairSub(token)).stream()
            .filter(sessao -> sessao.getRefreshTokenHash() != null && encoder.matches(hash, sessao.getRefreshTokenHash()))
            .findFirst()
            .orElseThrow(() -> new IllegalArgumentException("Sessão não encontrada ou refresh token já usado."));

        String access = jwtService.gerarAcessToken(s);
        String refresh = jwtService.gerarRefreshToken(s.getUsuario());

        s.setRefreshTokenHash(encoder.encode(digest(refresh)));
        s.setUltimoAcesso(Instant.now());

        return Map.of("access", access, "refresh", refresh);

    }

    @Transactional
    public void encerrar( UUID id ) { repository.deleteById(id); }

    @Transactional
    public void encerrarTodas( UUID usuarioId ) { repository.deleteByUsuarioId(usuarioId); }

    public boolean validar(UUID id) { return repository.existsById(id); }

    @Transactional
    public void atualizarFcmToken(UUID sessaoId, String fcmToken) {
        Sessao s = repository.findById(sessaoId)
                .orElseThrow(() -> new RuntimeException("Sessão não encontrada"));
        s.setFcmToken(fcmToken);
        repository.save(s);
    }

    private String digest(String token) {
        try {
            byte[] hash = MessageDigest.getInstance("SHA-256").digest(token.getBytes(StandardCharsets.UTF_8));
            return Base64.getEncoder().encodeToString(hash);
        } catch (NoSuchAlgorithmException e) { throw new RuntimeException(e); }
    }

}
