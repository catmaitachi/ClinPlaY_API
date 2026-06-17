package dev.clinplay.api.modules.clinics.repositories;

import java.util.Optional;
import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import dev.clinplay.api.modules.clinics.models.Clinica;

public interface ClinicaRepository extends JpaRepository<Clinica, UUID> {

    boolean existsByCnpj(String cnpj);

    boolean existsByTag(String tag);

    Optional<Clinica> findByTag(String tag);

    @Query("""
            SELECT c FROM Clinica c WHERE
            c.ativo = true AND
            LOWER(c.nome) LIKE LOWER(CONCAT('%', :nome, '%')) AND
            LOWER(c.especialidade) LIKE LOWER(CONCAT('%', :especialidade, '%')) AND
            (LOWER(c.cidade) LIKE LOWER(CONCAT('%', :localizacao, '%')) OR
             LOWER(c.uf) LIKE LOWER(CONCAT('%', :localizacao, '%')))
            """)
    Page<Clinica> listarComFiltros(
            @Param("nome") String nome,
            @Param("especialidade") String especialidade,
            @Param("localizacao") String localizacao,
            Pageable pageable);

}
