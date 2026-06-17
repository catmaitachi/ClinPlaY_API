package dev.clinplay.api.modules.requests.repositories;

import java.util.List;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

import dev.clinplay.api.modules.accounts.models.Profissional;
import dev.clinplay.api.modules.clinics.models.Clinica;
import dev.clinplay.api.modules.requests.models.SolicitacaoProfissional;
import dev.clinplay.api.modules.requests.models.enums.SituacaoSolicitacao;

public interface SolicitacaoProfissionalRepository extends JpaRepository<SolicitacaoProfissional, UUID> {

    boolean existsByClinicaAndProfissionalAndSituacao(Clinica clinica, Profissional profissional, SituacaoSolicitacao situacao);

    List<SolicitacaoProfissional> findByClinicaAndSituacao(Clinica clinica, SituacaoSolicitacao situacao);

    List<SolicitacaoProfissional> findByProfissional(Profissional profissional);

}
