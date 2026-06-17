package dev.clinplay.api.modules.clinics.repositories;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

import dev.clinplay.api.modules.accounts.models.Profissional;
import dev.clinplay.api.modules.clinics.models.Clinica;
import dev.clinplay.api.modules.clinics.models.ClinProfissional;

public interface ClinProfissionalRepository extends JpaRepository<ClinProfissional, UUID> {

    Optional<ClinProfissional> findByClinicaAndProfissional(Clinica clinica, Profissional profissional);

    Optional<ClinProfissional> findByClinicaIdAndProfissionalId(UUID clinicaId, UUID profissionalId);

    boolean existsByClinicaAndProfissional(Clinica clinica, Profissional profissional);

    List<ClinProfissional> findByClinica(Clinica clinica);

    List<ClinProfissional> findByProfissional(Profissional profissional);

    long countByClinica(Clinica clinica);

}
