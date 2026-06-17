package dev.clinplay.api.modules.treatment.services;

import java.util.UUID;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import dev.clinplay.api.modules.clinics.models.ClinProfissional;
import dev.clinplay.api.modules.clinics.repositories.ClinProfissionalRepository;
import dev.clinplay.api.modules.treatment.dtos.EditarExercicio;
import dev.clinplay.api.modules.treatment.dtos.ObterExercicio;
import dev.clinplay.api.modules.treatment.models.Exercicio;
import dev.clinplay.api.modules.treatment.repositories.ExercicioRepository;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ExercicioService {

    private final ExercicioRepository exercicioRepository;
    private final ClinProfissionalRepository clinProfissionalRepository;

    public ObterExercicio buscar(UUID exercicioId) {
        Exercicio exercicio = exercicioRepository.findById(exercicioId)
            .orElseThrow(() -> new IllegalArgumentException("Exercício não encontrado"));
        return new ObterExercicio(exercicio);
    }

    @Transactional
    public ObterExercicio editar(UUID profissionalId, UUID exercicioId, EditarExercicio dto) {

        Exercicio exercicio = exercicioRepository.findById(exercicioId)
            .orElseThrow(() -> new IllegalArgumentException("Exercício não encontrado"));

        // Busca por IDs explícitos para evitar comparação com proxy lazy do Hibernate
        UUID clinicaId = exercicio.getClinica().getId();
        ClinProfissional vinculo = clinProfissionalRepository.findByClinicaIdAndProfissionalId(clinicaId, profissionalId)
            .orElseThrow(() -> new IllegalArgumentException("Você não faz parte desta clínica"));

        var perm = vinculo.getPermissoes();
        boolean ehAdmin = perm != null && (perm.isDono() || perm.isAdminClinica()
            || perm.isAdminExercicios() || perm.isAdminPacientes() || perm.isAdminProfissionais());
        if (!ehAdmin)
            throw new IllegalArgumentException("Você não tem permissão para editar exercícios");

        exercicio.setNome(dto.getNome());
        exercicio.setDescricao(dto.getDescricao());
        exercicio.setVideoUrl(dto.getVideoUrl());
        exercicio.setConfigPadrao(dto.getConfigPadrao());

        return new ObterExercicio(exercicioRepository.save(exercicio));

    }

}
