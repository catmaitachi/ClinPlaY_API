package dev.clinplay.api.modules.subscriptions.services;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import dev.clinplay.api.modules.accounts.models.Profissional;
import dev.clinplay.api.modules.accounts.repositories.ProfissionalRepository;
import dev.clinplay.api.modules.clinics.models.Clinica;
import dev.clinplay.api.modules.clinics.repositories.ClinicaRepository;
import dev.clinplay.api.modules.clinics.repositories.ClinPacienteRepository;
import dev.clinplay.api.modules.clinics.repositories.ClinProfissionalRepository;
import dev.clinplay.api.modules.subscriptions.dtos.AdesaoPlano;
import dev.clinplay.api.modules.subscriptions.dtos.ObterAssinatura;
import dev.clinplay.api.modules.subscriptions.models.Assinatura;
import dev.clinplay.api.modules.subscriptions.models.Plano;
import dev.clinplay.api.modules.subscriptions.models.enums.StatusAssinatura;
import dev.clinplay.api.modules.subscriptions.repositories.AssinaturaRepository;
import dev.clinplay.api.modules.subscriptions.repositories.PlanoRepository;
import dev.clinplay.api.modules.treatment.repositories.ExercicioRepository;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class AssinaturaService {

    private final AssinaturaRepository repository;
    private final ClinicaRepository clinicaRepository;
    private final PlanoRepository planoRepository;
    private final ProfissionalRepository profissionalRepository;
    private final ClinProfissionalRepository clinProfissionalRepository;
    private final ClinPacienteRepository clinPacienteRepository;
    private final ExercicioRepository exercicioRepository;

    @Transactional
    public ObterAssinatura aderir(UUID profissionalId, UUID clinicaId, AdesaoPlano dto) {

        Clinica clinica = clinicaRepository.findById(clinicaId)
            .orElseThrow(() -> new IllegalArgumentException("Clínica não encontrada"));

        Profissional profissional = profissionalRepository.findById(profissionalId)
            .orElseThrow(() -> new IllegalArgumentException("Profissional não encontrado"));

        clinProfissionalRepository.findByClinicaAndProfissional(clinica, profissional)
            .filter(v -> v.getPermissoes().isDono())
            .orElseThrow(() -> new IllegalArgumentException("Apenas o dono da clínica pode gerenciar o plano"));

        Plano plano = planoRepository.findById(dto.getPlanoId())
            .orElseThrow(() -> new IllegalArgumentException("Plano não encontrado"));

        if (!plano.isDisponivel())
            throw new IllegalArgumentException("Este plano não está disponível para adesão");

        validarCapacidade(clinica, plano);

        Assinatura assinatura = repository.findByClinica(clinica).orElseGet(Assinatura::new);

        clinica.setAtivo(true);

        assinatura.setClinica(clinica);
        assinatura.setPlano(plano);
        assinatura.setInicio(LocalDate.now());
        assinatura.setValidade(dto.getValidade());
        assinatura.setStatus(StatusAssinatura.ATIVA);

        return new ObterAssinatura(repository.save(assinatura));

    }

    private void validarCapacidade(Clinica clinica, Plano plano) {

        List<String> violacoes = new ArrayList<>();

        long profissionais = clinProfissionalRepository.countByClinica(clinica);
        if (profissionais > plano.getMaxProfissionais())
            violacoes.add("Profissionais: o plano permite " + plano.getMaxProfissionais() + ", a clínica possui " + profissionais);

        long pacientes = clinPacienteRepository.countByClinica(clinica);
        if (pacientes > plano.getMaxPacientes())
            violacoes.add("Pacientes: o plano permite " + plano.getMaxPacientes() + ", a clínica possui " + pacientes);

        long exercicios = exercicioRepository.countByClinica(clinica);
        if (exercicios > plano.getMaxExercicios())
            violacoes.add("Exercícios: o plano permite " + plano.getMaxExercicios() + ", a clínica possui " + exercicios);

        if (!violacoes.isEmpty())
            throw new IllegalArgumentException("O plano selecionado não comporta os dados atuais da clínica: " + String.join(" | ", violacoes));

    }

    @Transactional
    public ObterAssinatura cancelar(UUID profissionalId, UUID clinicaId) {

        Clinica clinica = clinicaRepository.findById(clinicaId)
            .orElseThrow(() -> new IllegalArgumentException("Clínica não encontrada"));

        Profissional profissional = profissionalRepository.findById(profissionalId)
            .orElseThrow(() -> new IllegalArgumentException("Profissional não encontrado"));

        clinProfissionalRepository.findByClinicaAndProfissional(clinica, profissional)
            .filter(v -> v.getPermissoes().isDono())
            .orElseThrow(() -> new IllegalArgumentException("Apenas o dono da clínica pode cancelar o plano"));

        Assinatura assinatura = repository.findByClinica(clinica)
            .orElseThrow(() -> new IllegalArgumentException("Esta clínica não possui assinatura"));

        if (assinatura.getStatus() == StatusAssinatura.CANCELADA)
            throw new IllegalArgumentException("A assinatura já está cancelada");

        assinatura.setStatus(StatusAssinatura.CANCELADA);
        clinica.setAtivo(false);
        clinicaRepository.save(clinica);

        return new ObterAssinatura(repository.save(assinatura));

    }

}
