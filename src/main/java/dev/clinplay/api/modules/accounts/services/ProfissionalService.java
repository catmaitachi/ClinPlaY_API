package dev.clinplay.api.modules.accounts.services;

import java.util.UUID;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import dev.clinplay.api.modules.accounts.dtos.AtualizarProfissional;
import dev.clinplay.api.modules.accounts.dtos.CadastroProfissional;
import dev.clinplay.api.modules.accounts.dtos.ObterProfissional;
import dev.clinplay.api.modules.accounts.models.Profissional;
import dev.clinplay.api.modules.accounts.models.embeddables.Conselho;
import dev.clinplay.api.modules.accounts.models.enums.Perfil;
import dev.clinplay.api.modules.accounts.repositories.ProfissionalRepository;
import dev.clinplay.api.modules.auth.models.Login;
import dev.clinplay.api.modules.auth.services.LoginService;
import dev.clinplay.api.modules.clinics.repositories.ClinProfissionalRepository;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ProfissionalService {

    private final ProfissionalRepository repository;
    private final UsuarioService usuarioService;
    private final LoginService loginService;
    private final ClinProfissionalRepository clinProfissionalRepository;

    public boolean verificarCrefito(String crefito) { return repository.existsByCrefito(crefito); }

    public ObterProfissional obter(UUID id) {

        Profissional p = repository.findById(id)
            .orElseThrow(() -> new IllegalArgumentException("Profissional não encontrado"));

        return new ObterProfissional(p);

    }

    @Transactional
    public ObterProfissional atualizar(UUID id, AtualizarProfissional dto) {

        if (repository.existsByCrefitoAndIdNot(dto.getCrefito(), id)) throw new IllegalArgumentException("CREFITO já cadastrado");
        if (usuarioService.verificarEmailExcluindo(dto.getEmail(), id)) throw new IllegalArgumentException("Email já cadastrado");

        Profissional p = repository.findById(id)
            .orElseThrow(() -> new IllegalArgumentException("Profissional não encontrado"));

        p.setNome(dto.getNome());
        p.setTelefone(dto.getTelefone());
        p.setNascimento(dto.getNascimento());
        p.setEmail(dto.getEmail());
        p.setAvatar(dto.getAvatar());
        p.setCrefito(dto.getCrefito());
        p.setEspecialidade(dto.getEspecialidade());
        p.setConselho(Conselho.builder()
            .nome(dto.getConselhoNome())
            .numero(dto.getConselhoNumero())
            .uf(dto.getConselhoUf())
            .build());

        return new ObterProfissional(repository.save(p));

    }

    @Transactional
    public void inativar(UUID id) {

        Profissional p = repository.findById(id)
            .orElseThrow(() -> new IllegalArgumentException("Profissional não encontrado"));

        clinProfissionalRepository.deleteAll(clinProfissionalRepository.findByProfissional(p));
        p.setAtivo(false);
        repository.save(p);

    }

    @Transactional
    public Profissional cadastrar(CadastroProfissional dto, UUID loginId) {

        if (verificarCrefito(dto.getCrefito())) throw new IllegalArgumentException("CREFITO já cadastrado");
        if (usuarioService.verificarEmail(dto.getEmail())) throw new IllegalArgumentException("Email já cadastrado");

        Login login = loginService.buscar(loginId);

        Profissional p = new Profissional();

        p.setPerfil(Perfil.PROFISSIONAL);
        p.setEmail(dto.getEmail());
        p.setNome(dto.getNome());
        p.setTelefone(dto.getTelefone());
        p.setNascimento(dto.getNascimento());
        p.setAvatar(dto.getAvatar());
        p.setAtivo(true);

        p.setCrefito(dto.getCrefito());
        p.setEspecialidade(dto.getEspecialidade());

        p.setConselho(Conselho.builder()
            .nome(dto.getConselhoNome())
            .numero(dto.getConselhoNumero())
            .uf(dto.getConselhoUf())
            .build());

        Profissional saved = repository.save(p);
        login.setUsuario(saved);

        return saved;

    }

}
