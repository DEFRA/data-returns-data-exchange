package uk.gov.ea.datareturns.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import uk.gov.ea.datareturns.domain.jpa.entities.masterdata.MasterDataEntity;
import uk.gov.ea.datareturns.domain.jpa.repositories.BaseRepository;
import uk.gov.ea.datareturns.domain.jpa.repositories.masterdata.MasterDataRepository;

import javax.inject.Inject;
import javax.persistence.EntityManagerFactory;
import javax.persistence.metamodel.EntityType;
import java.io.Serializable;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Configuration for Spring JPA Repositories
 *
 * @author Sam Gardner-Dell
 */
@Configuration
@EnableJpaAuditing
@EnableJpaRepositories(basePackages = "uk.gov.ea.datareturns.domain.jpa.repositories", considerNestedRepositories = true, repositoryBaseClass = BaseRepository.BaseRepositoryImpl.class)
public class JpaRepositoryConfiguration {
    protected static final Logger LOGGER = LoggerFactory.getLogger(JpaRepositoryConfiguration.class);
    private final Map<Class<?>, BaseRepository<?, ? extends Serializable>> repositoryMap = new HashMap<>();
    private final Map<Class<? extends MasterDataEntity>, MasterDataRepository<? extends MasterDataEntity>> masterDataRepositoryMap = new HashMap<>();

    @Inject
    public JpaRepositoryConfiguration(List<? extends BaseRepository<?, ? extends Serializable>> repositories,
            final EntityManagerFactory factory) {
        for (BaseRepository<?, ? extends Serializable> repository : repositories) {
            this.repositoryMap.put(repository.getDomainClass(), repository);

            if (repository instanceof MasterDataRepository) {
                MasterDataRepository<?> masterDataRepository = (MasterDataRepository<?>) repository;
                this.masterDataRepositoryMap.put(masterDataRepository.getDomainClass(), masterDataRepository);
            }
        }

        List<Class<?>> entityClasses = factory.getMetamodel().getEntities()
                .stream().map(EntityType::getJavaType).collect(Collectors.toList());
        for (Class<?> entityClass : entityClasses) {
            if (!this.repositoryMap.containsKey(entityClass)) {
                LOGGER.warn("Couldn't find spring data repository for entity class: " + entityClass.getName());
            }
        }
    }

    @SuppressWarnings("unchecked")
    public <E> BaseRepository<E, ? extends Serializable> getRepository(Class<E> domainClass) {
        return (BaseRepository<E, ? extends Serializable>) repositoryMap.get(domainClass);
    }

    @SuppressWarnings("unchecked")
    public <E extends MasterDataEntity> MasterDataRepository<E> getMasterDataRepository(Class<E> domainClass) {
        return (MasterDataRepository<E>) repositoryMap.get(domainClass);
    }
}