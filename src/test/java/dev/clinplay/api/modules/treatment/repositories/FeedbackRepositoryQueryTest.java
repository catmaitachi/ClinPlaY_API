package dev.clinplay.api.modules.treatment.repositories;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;

import org.hibernate.SessionFactory;
import org.hibernate.boot.MetadataSources;
import org.hibernate.boot.registry.StandardServiceRegistryBuilder;
import org.junit.jupiter.api.Test;
import org.springframework.context.annotation.ClassPathScanningCandidateComponentProvider;
import org.springframework.core.type.filter.AnnotationTypeFilter;

import dev.clinplay.api.modules.treatment.dtos.ObterRankingPaciente;
import jakarta.persistence.Entity;

/**
 * Valida a JPQL do ranking contra o mapeamento real das entidades, sem banco.
 * Uma query inválida só apareceria no boot da API em produção.
 */
class FeedbackRepositoryQueryTest {

    @Test
    void queryDoRankingCompila() throws Exception {

        var registry = new StandardServiceRegistryBuilder()
            .applySetting("hibernate.dialect", "org.hibernate.dialect.PostgreSQLDialect")
            .applySetting("hibernate.boot.allow_jdbc_metadata_access", false)
            .applySetting("hibernate.hbm2ddl.auto", "none")
            .build();

        var fontes = new MetadataSources(registry);
        var scanner = new ClassPathScanningCandidateComponentProvider(false);
        scanner.addIncludeFilter(new AnnotationTypeFilter(Entity.class));
        for (var bean : scanner.findCandidateComponents("dev.clinplay.api"))
            fontes.addAnnotatedClass(Class.forName(bean.getBeanClassName()));

        try (SessionFactory sf = fontes.buildMetadata().buildSessionFactory();
             var sessao = sf.openSession()) {
            assertDoesNotThrow(() -> sessao.createQuery(FeedbackRepository.RANKING, ObterRankingPaciente.class));
        }

    }

}
