package dev.clinplay.api.modules.clinics.services;

import java.util.List;
import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import dev.clinplay.api.modules.accounts.models.Paciente;
import dev.clinplay.api.modules.accounts.models.Profissional;
import dev.clinplay.api.modules.accounts.models.enums.Perfil;
import dev.clinplay.api.modules.accounts.repositories.PacienteRepository;
import dev.clinplay.api.modules.accounts.repositories.ProfissionalRepository;
import dev.clinplay.api.modules.accounts.repositories.UsuarioRepository;
import dev.clinplay.api.modules.clinics.dtos.AtualizarPermissoes;
import dev.clinplay.api.modules.clinics.dtos.CadastroClinica;
import dev.clinplay.api.modules.clinics.dtos.EditarClinica;
import dev.clinplay.api.modules.clinics.dtos.ListarClinica;
import dev.clinplay.api.modules.clinics.dtos.ObterClinPaciente;
import dev.clinplay.api.modules.clinics.dtos.ObterClinProfissional;
import dev.clinplay.api.modules.clinics.dtos.ObterClinica;
import dev.clinplay.api.modules.clinics.dtos.ObterClinicasUsuario;
import dev.clinplay.api.modules.treatment.dtos.ObterExercicio;
import dev.clinplay.api.modules.treatment.models.Exercicio;
import dev.clinplay.api.modules.treatment.repositories.ExercicioRepository;
import dev.clinplay.api.modules.clinics.models.Clinica;
import dev.clinplay.api.modules.clinics.models.ClinPaciente;
import dev.clinplay.api.modules.clinics.models.ClinProfissional;
import dev.clinplay.api.modules.clinics.models.embeddables.Permissoes;
import dev.clinplay.api.modules.clinics.repositories.ClinicaRepository;
import dev.clinplay.api.modules.clinics.repositories.ClinPacienteRepository;
import dev.clinplay.api.modules.clinics.repositories.ClinProfissionalRepository;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ClinicaService {

    private final ClinicaRepository repository;
    private final UsuarioRepository usuarioRepository;
    private final ProfissionalRepository profissionalRepository;
    private final PacienteRepository pacienteRepository;
    private final ClinProfissionalRepository clinProfissionalRepository;
    private final ClinPacienteRepository clinPacienteRepository;
    private final ExercicioRepository exercicioRepository;

    @Transactional(readOnly = true)
    public Page<ListarClinica> listar(String nome, String especialidade, String localizacao, Pageable pageable) {
        return repository.listarComFiltros(
            nome != null && !nome.isBlank() ? nome.trim() : "",
            especialidade != null && !especialidade.isBlank() ? especialidade.trim() : "",
            localizacao != null && !localizacao.isBlank() ? localizacao.trim() : "",
            pageable
        ).map(ListarClinica::new);
    }

    @Transactional(readOnly = true)
    public List<ObterClinicasUsuario> listarMinhasClinicas(UUID usuarioId) {

        Perfil perfil = usuarioRepository.findPerfilById(usuarioId)
            .orElseThrow(() -> new IllegalArgumentException("Usuário não encontrado"));

        if (perfil == Perfil.PACIENTE) {
            Paciente paciente = pacienteRepository.findById(usuarioId)
                .orElseThrow(() -> new IllegalArgumentException("Paciente não encontrado"));
            return clinPacienteRepository.findByPaciente(paciente).stream()
                .filter(cp -> cp.getClinica().isAtivo())
                .map(ObterClinicasUsuario::new)
                .toList();
        } else {
            Profissional profissional = profissionalRepository.findById(usuarioId)
                .orElseThrow(() -> new IllegalArgumentException("Profissional não encontrado"));
            return clinProfissionalRepository.findByProfissional(profissional).stream()
                .filter(cp -> cp.getClinica().isAtivo() || cp.getPermissoes().isDono())
                .map(ObterClinicasUsuario::new)
                .toList();
        }

    }

    @Transactional
    public ObterClinica editar(UUID profissionalId, UUID clinicaId, EditarClinica dto) {

        Clinica clinica = repository.findById(clinicaId)
            .orElseThrow(() -> new IllegalArgumentException("Clínica não encontrada"));

        Profissional profissional = profissionalRepository.findById(profissionalId)
            .orElseThrow(() -> new IllegalArgumentException("Profissional não encontrado"));

        ClinProfissional vinculo = clinProfissionalRepository.findByClinicaAndProfissional(clinica, profissional)
            .orElseThrow(() -> new IllegalArgumentException("Você não faz parte desta clínica"));

        Permissoes p = vinculo.getPermissoes();
        if (!p.isDono() && !p.isAdminClinica())
            throw new IllegalArgumentException("Você não tem permissão para editar esta clínica");

        clinica.setNome(dto.getNome());
        clinica.setEspecialidade(dto.getEspecialidade());
        clinica.setUf(dto.getUf());
        clinica.setCidade(dto.getCidade());

        return new ObterClinica(repository.save(clinica));

    }

    public ObterClinica buscarPorTag(String tag) {
        Clinica clinica = repository.findByTag(tag)
            .orElseThrow(() -> new IllegalArgumentException("Clínica não encontrada"));
        if (!clinica.isAtivo()) throw new IllegalArgumentException("Clínica não encontrada");
        return new ObterClinica(clinica);
    }

    @Transactional
    public ObterClinica cadastrar(UUID profissionalId, CadastroClinica dto) {

        if (repository.existsByCnpj(dto.getCnpj()))
            throw new IllegalArgumentException("Já existe uma clínica com o CNPJ informado");

        if (repository.existsByTag(dto.getTag()))
            throw new IllegalArgumentException("Já existe uma clínica com a tag '" + dto.getTag() + "'");

        Profissional profissional = profissionalRepository.findById(profissionalId)
            .orElseThrow(() -> new IllegalArgumentException("Profissional não encontrado"));

        Clinica c = new Clinica();
        c.setNome(dto.getNome());
        c.setCnpj(dto.getCnpj());
        c.setTag(dto.getTag());
        c.setEspecialidade(dto.getEspecialidade());
        c.setUf(dto.getUf());
        c.setCidade(dto.getCidade());

        ClinProfissional cp = new ClinProfissional();
        cp.setClinica(c);
        cp.setProfissional(profissional);

        Permissoes permissoes = Permissoes.builder()
            .dono(true)
            .adminClinica(true)
            .adminExercicios(true)
            .adminPacientes(true)
            .adminProfissionais(true)
            .build();

        cp.setPermissoes(permissoes);

        c.setProfissionais(List.of(cp));

        return new ObterClinica(repository.save(c));

    }

    @Transactional(readOnly = true)
    public List<ObterExercicio> listarExercicios(UUID profissionalId, UUID clinicaId) {

        Clinica clinica = repository.findById(clinicaId)
            .orElseThrow(() -> new IllegalArgumentException("Clínica não encontrada"));

        Profissional profissional = profissionalRepository.findById(profissionalId)
            .orElseThrow(() -> new IllegalArgumentException("Profissional não encontrado"));

        clinProfissionalRepository.findByClinicaAndProfissional(clinica, profissional)
            .orElseThrow(() -> new IllegalArgumentException("Você não faz parte desta clínica"));

        return exercicioRepository.findByClinica(clinica).stream()
            .map(ObterExercicio::new)
            .toList();

    }

    public List<ObterClinPaciente> listarPacientes(UUID profissionalId, UUID clinicaId) {

        Clinica clinica = repository.findById(clinicaId)
            .orElseThrow(() -> new IllegalArgumentException("Clínica não encontrada"));

        Profissional profissional = profissionalRepository.findById(profissionalId)
            .orElseThrow(() -> new IllegalArgumentException("Profissional não encontrado"));

        clinProfissionalRepository.findByClinicaAndProfissional(clinica, profissional)
            .orElseThrow(() -> new IllegalArgumentException("Você não faz parte desta clínica"));

        return clinPacienteRepository.findByClinica(clinica).stream()
            .map(ObterClinPaciente::new)
            .toList();

    }

    public List<ObterClinProfissional> listarProfissionais(UUID profissionalId, UUID clinicaId) {

        Clinica clinica = repository.findById(clinicaId)
            .orElseThrow(() -> new IllegalArgumentException("Clínica não encontrada"));

        Profissional profissional = profissionalRepository.findById(profissionalId)
            .orElseThrow(() -> new IllegalArgumentException("Profissional não encontrado"));

        clinProfissionalRepository.findByClinicaAndProfissional(clinica, profissional)
            .orElseThrow(() -> new IllegalArgumentException("Você não faz parte desta clínica"));

        return clinProfissionalRepository.findByClinica(clinica).stream()
            .map(ObterClinProfissional::new)
            .toList();

    }

    @Transactional
    public void atualizarPermissoes(UUID solicitanteId, UUID clinicaId, UUID profissionalId, AtualizarPermissoes dto) {

        if (solicitanteId.equals(profissionalId))
            throw new IllegalArgumentException("Você não pode alterar suas próprias permissões");

        Clinica clinica = repository.findById(clinicaId)
            .orElseThrow(() -> new IllegalArgumentException("Clínica não encontrada"));

        Profissional solicitante = profissionalRepository.findById(solicitanteId)
            .orElseThrow(() -> new IllegalArgumentException("Profissional não encontrado"));

        ClinProfissional vinculoSolicitante = clinProfissionalRepository.findByClinicaAndProfissional(clinica, solicitante)
            .orElseThrow(() -> new IllegalArgumentException("Você não faz parte desta clínica"));

        Permissoes ps = vinculoSolicitante.getPermissoes();
        if (!ps.isDono() && !ps.isAdminClinica() && !ps.isAdminProfissionais())
            throw new IllegalArgumentException("Você não tem permissão para editar permissões nesta clínica");

        Profissional alvo = profissionalRepository.findById(profissionalId)
            .orElseThrow(() -> new IllegalArgumentException("Profissional não encontrado"));

        ClinProfissional vinculoAlvo = clinProfissionalRepository.findByClinicaAndProfissional(clinica, alvo)
            .orElseThrow(() -> new IllegalArgumentException("Este profissional não faz parte desta clínica"));

        if (vinculoAlvo.getPermissoes().isDono())
            throw new IllegalArgumentException("As permissões do dono da clínica não podem ser alteradas");

        Permissoes novas = vinculoAlvo.getPermissoes();

        if (ps.isDono()) {
            novas.setAdminExercicios(dto.isAdminExercicios());
            novas.setAdminPacientes(dto.isAdminPacientes());
            novas.setAdminProfissionais(dto.isAdminProfissionais());
            novas.setAdminClinica(dto.isAdminClinica());
        } else if (ps.isAdminClinica()) {
            if (vinculoAlvo.getPermissoes().isAdminClinica())
                throw new IllegalArgumentException("Você não tem permissão para editar as permissões de outro administrador");
            novas.setAdminExercicios(dto.isAdminExercicios());
            novas.setAdminPacientes(dto.isAdminPacientes());
            novas.setAdminProfissionais(dto.isAdminProfissionais());
            novas.setAdminClinica(dto.isAdminClinica());
        } else {
            if (vinculoAlvo.getPermissoes().isAdminClinica())
                throw new IllegalArgumentException("Você não tem permissão para editar as permissões deste usuário");
            novas.setAdminExercicios(dto.isAdminExercicios());
            novas.setAdminPacientes(dto.isAdminPacientes());
        }

        clinProfissionalRepository.save(vinculoAlvo);

    }

    @Transactional
    public void desvincularPaciente(UUID solicitanteId, UUID clinicaId, UUID pacienteId) {

        Clinica clinica = repository.findById(clinicaId)
            .orElseThrow(() -> new IllegalArgumentException("Clínica não encontrada"));

        Profissional solicitante = profissionalRepository.findById(solicitanteId)
            .orElseThrow(() -> new IllegalArgumentException("Profissional não encontrado"));

        ClinProfissional vinculo = clinProfissionalRepository.findByClinicaAndProfissional(clinica, solicitante)
            .orElseThrow(() -> new IllegalArgumentException("Você não faz parte desta clínica"));

        Permissoes p = vinculo.getPermissoes();
        if (!p.isDono() && !p.isAdminPacientes())
            throw new IllegalArgumentException("Você não tem permissão para desvincular pacientes");

        Paciente paciente = pacienteRepository.findById(pacienteId)
            .orElseThrow(() -> new IllegalArgumentException("Paciente não encontrado"));

        ClinPaciente vinculoPaciente = clinPacienteRepository.findByClinicaAndPaciente(clinica, paciente)
            .orElseThrow(() -> new IllegalArgumentException("Este paciente não faz parte desta clínica"));

        clinPacienteRepository.delete(vinculoPaciente);

    }

    @Transactional
    public void excluirExercicio(UUID profissionalId, UUID clinicaId, UUID exercicioId) {

        Clinica clinica = repository.findById(clinicaId)
            .orElseThrow(() -> new IllegalArgumentException("Clínica não encontrada"));

        Profissional profissional = profissionalRepository.findById(profissionalId)
            .orElseThrow(() -> new IllegalArgumentException("Profissional não encontrado"));

        ClinProfissional vinculo = clinProfissionalRepository.findByClinicaAndProfissional(clinica, profissional)
            .orElseThrow(() -> new IllegalArgumentException("Você não faz parte desta clínica"));

        if (!vinculo.getPermissoes().isDono() && !vinculo.getPermissoes().isAdminExercicios())
            throw new IllegalArgumentException("Você não tem permissão para excluir exercícios");

        Exercicio exercicio = exercicioRepository.findById(exercicioId)
            .orElseThrow(() -> new IllegalArgumentException("Exercício não encontrado"));

        if (!exercicio.getClinica().getId().equals(clinicaId))
            throw new IllegalArgumentException("O exercício não pertence a esta clínica");

        exercicioRepository.delete(exercicio);

    }

    @Transactional
    public void desvincularProfissional(UUID solicitanteId, UUID clinicaId, UUID profissionalId) {

        Clinica clinica = repository.findById(clinicaId)
            .orElseThrow(() -> new IllegalArgumentException("Clínica não encontrada"));

        Profissional solicitante = profissionalRepository.findById(solicitanteId)
            .orElseThrow(() -> new IllegalArgumentException("Profissional não encontrado"));

        ClinProfissional vinculoSolicitante = clinProfissionalRepository.findByClinicaAndProfissional(clinica, solicitante)
            .orElseThrow(() -> new IllegalArgumentException("Você não faz parte desta clínica"));

        Permissoes p = vinculoSolicitante.getPermissoes();
        if (!p.isDono() && !p.isAdminProfissionais())
            throw new IllegalArgumentException("Você não tem permissão para desvincular profissionais");

        Profissional alvo = profissionalRepository.findById(profissionalId)
            .orElseThrow(() -> new IllegalArgumentException("Profissional não encontrado"));

        ClinProfissional vinculoAlvo = clinProfissionalRepository.findByClinicaAndProfissional(clinica, alvo)
            .orElseThrow(() -> new IllegalArgumentException("Este profissional não faz parte desta clínica"));

        if (vinculoAlvo.getPermissoes().isDono())
            throw new IllegalArgumentException("O dono da clínica não pode ser desvinculado");

        clinProfissionalRepository.delete(vinculoAlvo);

    }

}
