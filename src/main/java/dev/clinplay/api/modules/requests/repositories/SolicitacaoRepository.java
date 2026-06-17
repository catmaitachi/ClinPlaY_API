package dev.clinplay.api.modules.requests.repositories;

import java.util.List;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

import dev.clinplay.api.modules.clinics.models.Clinica;
import dev.clinplay.api.modules.requests.models.Solicitacao;
import dev.clinplay.api.modules.requests.models.enums.SituacaoSolicitacao;

public interface SolicitacaoRepository extends JpaRepository<Solicitacao, UUID> {

    List<Solicitacao> findByClinicaAndSituacao(Clinica clinica, SituacaoSolicitacao situacao);

}
