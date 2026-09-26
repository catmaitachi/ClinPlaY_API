package dev.clinplay.api.modules.auth.services;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import dev.clinplay.api.modules.accounts.models.Paciente;
import dev.clinplay.api.modules.accounts.models.embeddables.Origem;
import dev.clinplay.api.modules.auth.models.Sessao;
import dev.clinplay.api.modules.auth.repositories.SessaoRepository;
import dev.clinplay.api.modules.security.jwt.JwtService;

/**
 * No celular o IP muda a cada troca de rede. O refresh não pode depender
 * dele, senão o PWA reaberto depois de 30 min cai no login.
 */
class SessaoServiceTest {

    private final SessaoRepository repository = mock(SessaoRepository.class);
    private final JwtService jwt = mock(JwtService.class);
    private final SessaoService service = new SessaoService(repository, new BCryptPasswordEncoder(4), jwt);

    private final UUID usuarioId = UUID.randomUUID();
    private final Origem wifi = new Origem("200.1.1.1", "Android - Chrome");

    private Sessao sessao;

    @BeforeEach
    void preparar() {
        Paciente usuario = new Paciente();
        usuario.setId(usuarioId);

        when(repository.save(any(Sessao.class))).thenAnswer(i -> i.getArgument(0));
        when(jwt.extrairSub(any())).thenReturn(usuarioId);
        when(jwt.gerarAcessToken(any())).thenReturn("novo-access");
        when(jwt.gerarRefreshToken(any())).thenReturn("novo-refresh");

        when(repository.findByUsuarioIdAndOrigem(usuarioId, wifi)).thenReturn(java.util.Optional.empty());
        sessao = service.iniciar(usuario, "refresh-original", wifi);
        when(repository.findAllByUsuarioIdOrderByUltimoAcessoDesc(usuarioId)).thenReturn(List.of(sessao));
    }

    @Test
    void renovaSemDependerDoIpDoLogin() {
        Map<String, String> tokens = service.refresh("refresh-original");

        assertEquals("novo-access", tokens.get("access"));
        assertEquals("novo-refresh", tokens.get("refresh"));
    }

    @Test
    void recusaRefreshQueNaoPertenceANenhumaSessao() {
        assertThrows(IllegalArgumentException.class, () -> service.refresh("refresh-forjado"));
    }

}
