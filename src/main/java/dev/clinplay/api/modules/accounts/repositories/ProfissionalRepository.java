package dev.clinplay.api.modules.accounts.repositories;

import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

import dev.clinplay.api.modules.accounts.models.Profissional;

public interface ProfissionalRepository extends JpaRepository<Profissional, UUID> {

    public boolean existsByCrefito(String crefito);

    public boolean existsByCrefitoAndIdNot(String crefito, UUID id);

}
