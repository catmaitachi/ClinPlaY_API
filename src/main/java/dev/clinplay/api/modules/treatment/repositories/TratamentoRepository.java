package dev.clinplay.api.modules.treatment.repositories;

import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

import dev.clinplay.api.modules.treatment.models.Tratamento;

public interface TratamentoRepository extends JpaRepository<Tratamento, UUID> {
}
