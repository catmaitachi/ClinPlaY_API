package dev.clinplay.api.modules.clinics.repositories;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

import dev.clinplay.api.modules.accounts.models.Paciente;
import dev.clinplay.api.modules.clinics.models.Clinica;
import dev.clinplay.api.modules.clinics.models.ClinPaciente;

public interface ClinPacienteRepository extends JpaRepository<ClinPaciente, UUID> {

    boolean existsByClinicaAndPaciente(Clinica clinica, Paciente paciente);

    Optional<ClinPaciente> findByClinicaAndPaciente(Clinica clinica, Paciente paciente);

    List<ClinPaciente> findByClinica(Clinica clinica);

    List<ClinPaciente> findByPaciente(Paciente paciente);

    long countByClinica(Clinica clinica);

}
