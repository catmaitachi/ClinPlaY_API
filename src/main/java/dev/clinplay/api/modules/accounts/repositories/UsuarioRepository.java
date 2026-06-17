package dev.clinplay.api.modules.accounts.repositories;

import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import dev.clinplay.api.modules.accounts.models.Usuario;
import dev.clinplay.api.modules.accounts.models.enums.Perfil;

public interface UsuarioRepository extends JpaRepository<Usuario, UUID> {

    @Query("SELECT u.perfil FROM Usuario u WHERE u.id = :id")
    public Optional<Perfil> findPerfilById(UUID id);

    public boolean existsByEmail(String email);

    public boolean existsByEmailAndIdNot(String email, UUID id);

}
