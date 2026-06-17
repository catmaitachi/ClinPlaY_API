package dev.clinplay.api.modules.records.services;

import java.util.UUID;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import dev.clinplay.api.modules.records.dtos.CadastroDocumento;
import dev.clinplay.api.modules.records.dtos.ObterDocumento;
import dev.clinplay.api.modules.records.models.Documento;
import dev.clinplay.api.modules.records.repositories.DocumentoRepository;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class DocumentoService {

    private final DocumentoRepository repository;

    public ObterDocumento obter(UUID id) {

        Documento d = repository.findById(id)
            .orElseThrow(() -> new IllegalArgumentException("Documento não encontrado"));

        return new ObterDocumento(d);

    }

    @Transactional
    public ObterDocumento cadastrar(CadastroDocumento dto) {

        if (repository.existsByNomeAndVersao(dto.getNome(), dto.getVersao()))
            throw new IllegalArgumentException("Já existe um documento '" + dto.getNome() + "' na versão " + dto.getVersao());

        Documento d = new Documento();
        d.setNome(dto.getNome());
        d.setConteudo(dto.getConteudo());
        d.setVersao(dto.getVersao());

        return new ObterDocumento(repository.save(d));

    }

}
