package dev.clinplay.api.modules.treatment.repositories;

import java.util.List;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

import dev.clinplay.api.modules.treatment.models.Prescricao;
import dev.clinplay.api.modules.treatment.models.Tratamento;

public interface PrescricaoRepository extends JpaRepository<Prescricao, UUID> {

    List<Prescricao> findByTratamentoOrderByOrdem(Tratamento tratamento);

    int countByTratamento(Tratamento tratamento);

}
