package dev.clinplay.api.modules.treatment.repositories;

import java.time.LocalDateTime;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

import dev.clinplay.api.modules.treatment.models.Feedback;
import dev.clinplay.api.modules.treatment.models.Prescricao;

public interface FeedbackRepository extends JpaRepository<Feedback, UUID> {

    long countByPrescricaoAndQuandoBetween(Prescricao prescricao, LocalDateTime inicio, LocalDateTime fim);

}
