package dev.clinplay.api.modules.requests.repositories;

import java.util.List;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

import dev.clinplay.api.modules.accounts.models.Profissional;
import dev.clinplay.api.modules.clinics.models.Clinica;
import dev.clinplay.api.modules.requests.models.SolicitacaoExercicio;
import dev.clinplay.api.modules.requests.models.enums.SituacaoSolicitacao;

public interface SolicitacaoExercicioRepository extends JpaRepository<SolicitacaoExercicio, UUID> {

    List<SolicitacaoExercicio> findByClinicaAndSituacao(Clinica clinica, SituacaoSolicitacao situacao);

    List<SolicitacaoExercicio> findBySolicitante(Profissional solicitante);

}
