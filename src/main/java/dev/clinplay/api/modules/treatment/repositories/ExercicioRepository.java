package dev.clinplay.api.modules.treatment.repositories;

import java.util.List;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

import dev.clinplay.api.modules.clinics.models.Clinica;
import dev.clinplay.api.modules.treatment.models.Exercicio;

public interface ExercicioRepository extends JpaRepository<Exercicio, UUID> {

    List<Exercicio> findByClinica(Clinica clinica);

    long countByClinica(Clinica clinica);

}
