package dev.clinplay.api.modules.requests.repositories;

import java.util.List;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

import dev.clinplay.api.modules.accounts.models.Paciente;
import dev.clinplay.api.modules.clinics.models.Clinica;
import dev.clinplay.api.modules.requests.models.SolicitacaoPaciente;
import dev.clinplay.api.modules.requests.models.enums.SituacaoSolicitacao;

public interface SolicitacaoPacienteRepository extends JpaRepository<SolicitacaoPaciente, UUID> {

    boolean existsByClinicaAndPacienteAndSituacao(Clinica clinica, Paciente paciente, SituacaoSolicitacao situacao);

    List<SolicitacaoPaciente> findByClinicaAndSituacao(Clinica clinica, SituacaoSolicitacao situacao);

    List<SolicitacaoPaciente> findByPaciente(Paciente paciente);

}
