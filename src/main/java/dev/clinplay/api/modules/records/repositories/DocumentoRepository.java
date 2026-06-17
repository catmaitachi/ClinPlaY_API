package dev.clinplay.api.modules.records.repositories;

import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

import dev.clinplay.api.modules.records.models.Documento;

public interface DocumentoRepository extends JpaRepository<Documento, UUID> {

    boolean existsByNomeAndVersao(String nome, String versao);

}
