package dev.clinplay.api.modules.treatment.services;

import java.time.LocalDate;
import java.util.UUID;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import dev.clinplay.api.modules.accounts.models.Profissional;
import dev.clinplay.api.modules.accounts.repositories.ProfissionalRepository;
import dev.clinplay.api.modules.clinics.models.Clinica;
import dev.clinplay.api.modules.clinics.models.ClinPaciente;
import dev.clinplay.api.modules.clinics.models.ClinProfissional;
import dev.clinplay.api.modules.clinics.repositories.ClinicaRepository;
import dev.clinplay.api.modules.clinics.repositories.ClinPacienteRepository;
import dev.clinplay.api.modules.clinics.repositories.ClinProfissionalRepository;
import dev.clinplay.api.modules.treatment.dtos.CadastroTratamento;
import dev.clinplay.api.modules.treatment.dtos.ObterTratamento;
import dev.clinplay.api.modules.treatment.models.Tratamento;
import dev.clinplay.api.modules.treatment.repositories.TratamentoRepository;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class TratamentoService {

    private final TratamentoRepository repository;
    private final ClinicaRepository clinicaRepository;
    private final ProfissionalRepository profissionalRepository;
    private final ClinPacienteRepository clinPacienteRepository;
    private final ClinProfissionalRepository clinProfissionalRepository;

    @Transactional
    public ObterTratamento criar(UUID profissionalId, UUID clinicaId, CadastroTratamento dto) {

        Clinica clinica = clinicaRepository.findById(clinicaId)
            .orElseThrow(() -> new IllegalArgumentException("Clínica não encontrada"));

        Profissional profissional = profissionalRepository.findById(profissionalId)
            .orElseThrow(() -> new IllegalArgumentException("Profissional não encontrado"));

        ClinProfissional clinProf = clinProfissionalRepository.findByClinicaAndProfissional(clinica, profissional)
            .orElseThrow(() -> new IllegalArgumentException("Você não faz parte desta clínica"));

        ClinPaciente clinPaciente = clinPacienteRepository.findById(dto.getClinPacienteId())
            .orElseThrow(() -> new IllegalArgumentException("Vínculo paciente-clínica não encontrado"));

        if (!clinPaciente.getClinica().getId().equals(clinicaId))
            throw new IllegalArgumentException("Este paciente não pertence a esta clínica");

        Tratamento t = new Tratamento();
        t.setClinica(clinica);
        t.setPaciente(clinPaciente);
        t.setProfissional(clinProf);
        t.setDescricao(dto.getDescricao());
        t.setInicio(dto.getInicio());
        t.setFim(dto.getFim());
        t.setLembreteConfig(dto.getLembreteConfig());

        return new ObterTratamento(repository.save(t));

    }

    @Transactional
    public void finalizar(UUID profissionalId, UUID tratamentoId) {

        Tratamento tratamento = repository.findById(tratamentoId)
            .orElseThrow(() -> new IllegalArgumentException("Tratamento não encontrado"));

        if (tratamento.getFim() != null)
            throw new IllegalArgumentException("Este tratamento já foi finalizado");

        Clinica clinica = tratamento.getClinica();

        Profissional profissional = profissionalRepository.findById(profissionalId)
            .orElseThrow(() -> new IllegalArgumentException("Profissional não encontrado"));

        ClinProfissional vinculo = clinProfissionalRepository.findByClinicaAndProfissional(clinica, profissional)
            .orElseThrow(() -> new IllegalArgumentException("Você não faz parte desta clínica"));

        boolean isResponsavel = tratamento.getProfissional() != null
            && tratamento.getProfissional().getId().equals(vinculo.getId());

        if (!isResponsavel && !vinculo.getPermissoes().isAdminPacientes())
            throw new IllegalArgumentException("Você não tem permissão para finalizar este tratamento");

        tratamento.setFim(LocalDate.now());
        repository.save(tratamento);

    }

}
