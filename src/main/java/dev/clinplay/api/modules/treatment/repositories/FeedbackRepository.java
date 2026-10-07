package dev.clinplay.api.modules.treatment.repositories;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import org.springframework.data.domain.Limit;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import dev.clinplay.api.modules.treatment.models.Feedback;
import dev.clinplay.api.modules.treatment.dtos.ObterRankingPaciente;
import dev.clinplay.api.modules.treatment.models.Prescricao;

public interface FeedbackRepository extends JpaRepository<Feedback, UUID> {

    long countByPrescricaoAndQuandoBetween(Prescricao prescricao, LocalDateTime inicio, LocalDateTime fim);

    /**
     * Cada feedback é um exercício concluído (é enviado ao fim do jogo).
     * Conta por paciente, só nos tratamentos do profissional nesta clínica.
     */
    String RANKING = """
        SELECT new dev.clinplay.api.modules.treatment.dtos.ObterRankingPaciente(
            pac.id, pac.nome, pac.avatar, COUNT(f), MAX(f.quando))
        FROM Feedback f
        JOIN f.prescricao p
        JOIN p.tratamento t
        JOIN t.paciente cp
        JOIN cp.paciente pac
        JOIN t.profissional cprof
        WHERE cprof.profissional.id = :profissionalId
          AND t.clinica.id = :clinicaId
          AND f.quando >= :desde
        GROUP BY pac.id, pac.nome, pac.avatar
        ORDER BY COUNT(f) DESC, MAX(f.quando) DESC
        """;

    @Query(RANKING)
    List<ObterRankingPaciente> ranking(UUID profissionalId, UUID clinicaId, LocalDateTime desde, Limit limite);

}
