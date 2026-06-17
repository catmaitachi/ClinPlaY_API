package dev.clinplay.api.modules.auth.repositories;

import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

import dev.clinplay.api.modules.accounts.models.embeddables.Origem;
import dev.clinplay.api.modules.auth.models.Sessao;

public interface SessaoRepository extends JpaRepository<Sessao, UUID> {

    Optional<Sessao> findByUsuarioIdAndOrigem(UUID usuarioId, Origem origem);

    Optional<Sessao> findTopByUsuarioIdAndFcmTokenNotNullOrderByUltimoAcessoDesc(UUID usuarioId);

    void deleteByUsuarioId(UUID usuarioId);

}
