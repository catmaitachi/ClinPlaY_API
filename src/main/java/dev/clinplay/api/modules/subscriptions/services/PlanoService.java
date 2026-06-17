package dev.clinplay.api.modules.subscriptions.services;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import dev.clinplay.api.modules.subscriptions.dtos.CadastroPlano;
import dev.clinplay.api.modules.subscriptions.dtos.ObterPlano;
import dev.clinplay.api.modules.subscriptions.models.Plano;
import dev.clinplay.api.modules.subscriptions.repositories.PlanoRepository;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class PlanoService {

    private final PlanoRepository repository;

    public List<ObterPlano> listar() {

        return repository.findByDisponivelTrue().stream()
            .map(ObterPlano::new)
            .toList();

    }

    @Transactional
    public ObterPlano cadastrar(CadastroPlano dto) {

        Plano p = new Plano();
        p.setNome(dto.getNome());
        p.setMaxProfissionais(dto.getMaxProfissionais());
        p.setMaxPacientes(dto.getMaxPacientes());
        p.setMaxExercicios(dto.getMaxExercicios());
        p.setDisponivel(dto.isDisponivel());

        return new ObterPlano(repository.save(p));

    }

}
