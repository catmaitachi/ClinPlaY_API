package dev.clinplay.api.modules.subscriptions.repositories;

import java.util.List;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

import dev.clinplay.api.modules.subscriptions.models.Plano;

public interface PlanoRepository extends JpaRepository<Plano, UUID> {

    boolean existsByNome(String nome);

    List<Plano> findByDisponivelTrue();

}
