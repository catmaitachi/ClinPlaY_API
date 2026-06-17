package dev.clinplay.api.modules.subscriptions.repositories;

import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

import dev.clinplay.api.modules.clinics.models.Clinica;
import dev.clinplay.api.modules.subscriptions.models.Assinatura;

public interface AssinaturaRepository extends JpaRepository<Assinatura, UUID> {

    Optional<Assinatura> findByClinica(Clinica clinica);

}
