package dev.clinplay.api.modules.accounts.repositories;

import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

import dev.clinplay.api.modules.accounts.models.Paciente;

public interface PacienteRepository extends JpaRepository<Paciente, UUID> {

    public boolean existsByCpf(String cpf);

    public boolean existsByCpfAndIdNot(String cpf, UUID id);

}
