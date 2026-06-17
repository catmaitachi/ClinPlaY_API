package dev.clinplay.api.modules.accounts.services;

import java.util.UUID;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import dev.clinplay.api.modules.accounts.dtos.AtualizarPaciente;
import dev.clinplay.api.modules.accounts.dtos.CadastroPaciente;
import dev.clinplay.api.modules.accounts.dtos.ObterPaciente;
import dev.clinplay.api.modules.accounts.models.Paciente;
import dev.clinplay.api.modules.accounts.models.enums.Perfil;
import dev.clinplay.api.modules.accounts.repositories.PacienteRepository;
import dev.clinplay.api.modules.auth.models.Login;
import dev.clinplay.api.modules.auth.services.LoginService;
import dev.clinplay.api.modules.clinics.repositories.ClinPacienteRepository;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class PacienteService {

    private final PacienteRepository repository;
    private final UsuarioService usuarioService;
    private final LoginService loginService;
    private final ClinPacienteRepository clinPacienteRepository;

    public boolean verificarCPF(String cpf) { return repository.existsByCpf(cpf); }

    public ObterPaciente obter(UUID id) {

        Paciente p = repository.findById(id)
            .orElseThrow(() -> new IllegalArgumentException("Paciente não encontrado"));

        return new ObterPaciente(p);

    }

    @Transactional
    public ObterPaciente atualizar(UUID id, AtualizarPaciente dto) {

        if (repository.existsByCpfAndIdNot(dto.getCpf(), id)) throw new IllegalArgumentException("CPF já cadastrado");
        if (usuarioService.verificarEmailExcluindo(dto.getEmail(), id)) throw new IllegalArgumentException("Email já cadastrado");

        Paciente p = repository.findById(id)
            .orElseThrow(() -> new IllegalArgumentException("Paciente não encontrado"));

        p.setNome(dto.getNome());
        p.setTelefone(dto.getTelefone());
        p.setNascimento(dto.getNascimento());
        p.setEmail(dto.getEmail());
        p.setAvatar(dto.getAvatar());
        p.setCpf(dto.getCpf());

        return new ObterPaciente(repository.save(p));

    }

    @Transactional
    public void inativar(UUID id) {

        Paciente p = repository.findById(id)
            .orElseThrow(() -> new IllegalArgumentException("Paciente não encontrado"));

        clinPacienteRepository.deleteAll(clinPacienteRepository.findByPaciente(p));
        p.setAtivo(false);
        repository.save(p);

    }

    @Transactional
    public Paciente cadastrar(CadastroPaciente dto, UUID loginId) {

            if (verificarCPF(dto.getCpf())) throw new IllegalArgumentException("CPF já cadastrado");
            if (usuarioService.verificarEmail(dto.getEmail())) throw new IllegalArgumentException("Email já cadastrado");

            Login login = loginService.buscar(loginId);

            Paciente p = new Paciente();
            p.setPerfil(Perfil.PACIENTE);
            p.setEmail(dto.getEmail());
            p.setNome(dto.getNome());
            p.setTelefone(dto.getTelefone());
            p.setNascimento(dto.getNascimento());
            p.setAvatar(dto.getAvatar());
            p.setAtivo(true);
            p.setCpf(dto.getCpf());

            Paciente saved = repository.save(p);
            login.setUsuario(saved);

            return saved;

    }

}
