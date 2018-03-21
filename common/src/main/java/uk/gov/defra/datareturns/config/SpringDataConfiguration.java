package uk.gov.defra.datareturns.config;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.transaction.annotation.EnableTransactionManagement;
import uk.gov.defra.datareturns.data.BaseRepository;

/**
 * Configuration for Spring JPA Repositories
 *
 * @author Sam Gardner-Dell
 */
@Configuration
@EnableTransactionManagement
@EntityScan(SpringDataConfiguration.DATA_PACKAGE)
@EnableJpaAuditing
@EnableJpaRepositories(
        basePackages = SpringDataConfiguration.DATA_PACKAGE,
        considerNestedRepositories = true,
        repositoryBaseClass = BaseRepository.BaseRepositoryImpl.class
)
@Slf4j
@RequiredArgsConstructor
@SuppressWarnings({"checkstyle:HideUtilityClassConstructor", "NonFinalUtilityClass"})
public class SpringDataConfiguration {
    public static final String DATA_PACKAGE = "uk.gov.defra.datareturns.data";
}
