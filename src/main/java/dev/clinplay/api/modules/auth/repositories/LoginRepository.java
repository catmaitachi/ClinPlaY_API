package dev.clinplay.api.modules.auth.repositories;

import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

import dev.clinplay.api.modules.auth.models.Login;
import dev.clinplay.api.modules.auth.models.enums.Provedor;

public interface LoginRepository extends JpaRepository<Login, UUID> {

    Optional<Login> findByIdentificadorAndProvedor(String identificador, Provedor provedor);

}
