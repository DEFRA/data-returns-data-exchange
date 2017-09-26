package uk.gov.ea.datareturns.domain.jpa.service;

import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;
import uk.gov.ea.datareturns.config.JpaRepositoryConfiguration;
import uk.gov.ea.datareturns.domain.jpa.entities.masterdata.MasterDataEntity;
import uk.gov.ea.datareturns.domain.jpa.repositories.masterdata.MasterDataRepository;

import javax.inject.Inject;

/**
 * Cache-backed master data lookup service
 *
 * @author Sam Gardner-Dell
 */
public interface MasterDataLookupService {
    /**
     * Provides lookup operations based on strict key rules.  For all operations, the natural key supplied must exactly match the natural
     * key of the entity being sought
     *
     * @return cache lookup operations based on strict key rules
     */
    Operations strict();

    /**
     * Provides lookup operations based on relaxed key rules.  For all operations, the natural key will be relaxed according to the specific
     * rules of the entity being operated on.  Relaxed keys use entity specific rules to relax the matching operation, such rules may
     * include relaxing case and standardising whitespace
     *
     * @return cache lookup operations based on relaxed key rules
     */
    Operations relaxed();

    /**
     * Lookup operations
     */
    interface Operations {
        /**
         * Determine if an entity exists for the given key.  This operation is lightweight as it only needs to check for the existence
         * of the key within the memory cache
         *
         * @param entityClass the entity class of the entity being sought
         * @param naturalKey the natural key to use in the lookup
         * @param <E> the generic type of the entity being sought
         * @return a boolean, true if the entity exists, false otherwise.
         */
        <E extends MasterDataEntity> boolean exists(Class<E> entityClass, String naturalKey);

        /**
         * Retrieve an entity for the given key.  This operation will determine if the key can be found in the memory cache and if found
         * will retrieve the entity from the persistence layer.
         *
         * @param entityClass the entity class of the entity being sought
         * @param naturalKey the natural key to use in the lookup
         * @param <E> the generic type of the entity being sought
         * @return the entity if found, null otherwise.
         */
        <E extends MasterDataEntity> E find(Class<E> entityClass, String naturalKey);
    }

    /**
     * {@link MasterDataLookupService} implementation
     *
     * @author Sam Gardner-Dell
     */
    @Service
    @Scope(BeanDefinition.SCOPE_SINGLETON) class MasterDataLookupServiceImpl implements MasterDataLookupService {
        private final JpaRepositoryConfiguration repositoryConfiguration;
        private final MasterDataNaturalKeyService masterDataNaturalKeyService;
        private final MasterDataCacheService masterDataCacheService;

        private final Operations strictOperations = new Operations() {
            @Override public <E extends MasterDataEntity> boolean exists(Class<E> entityClass, String naturalKey) {
                return masterDataCacheService.getStrictNaturalKeyToPkMap(entityClass).containsKey(naturalKey);
            }

            @Override public <E extends MasterDataEntity> E find(Class<E> entityClass, String naturalKey) {
                Long id = masterDataCacheService.getStrictNaturalKeyToPkMap(entityClass).get(naturalKey);
                E result = null;
                if (id != null) {
                    MasterDataRepository<E> repository = repositoryConfiguration.getMasterDataRepository(entityClass);
                    result = repository.getOne(id);
                }
                return result;
            }
        };

        private final Operations relaxedOperations = new Operations() {
            @Override public <E extends MasterDataEntity> boolean exists(Class<E> entityClass, String naturalKey) {
                String relaxedKey = masterDataNaturalKeyService.relaxKey(entityClass, naturalKey);
                return masterDataCacheService.getRelaxedNaturalKeyToPkMap(entityClass).containsKey(relaxedKey);
            }

            @Override public <E extends MasterDataEntity> E find(Class<E> entityClass, String naturalKey) {
                String relaxedKey = masterDataNaturalKeyService.relaxKey(entityClass, naturalKey);
                Long id = masterDataCacheService.getRelaxedNaturalKeyToPkMap(entityClass).get(relaxedKey);
                E result = null;
                if (id != null) {
                    MasterDataRepository<E> repository = repositoryConfiguration.getMasterDataRepository(entityClass);
                    result = repository.getOne(id);
                }
                return result;
            }
        };

        /**
         *
         *
         * @param repositoryConfiguration
         * @param masterDataNaturalKeyService
         * @param masterDataCacheService
         */
        @Inject
        public MasterDataLookupServiceImpl(JpaRepositoryConfiguration repositoryConfiguration,
                MasterDataNaturalKeyService masterDataNaturalKeyService,
                MasterDataCacheService masterDataCacheService) {
            this.repositoryConfiguration = repositoryConfiguration;
            this.masterDataNaturalKeyService = masterDataNaturalKeyService;
            this.masterDataCacheService = masterDataCacheService;
        }

        @Override public Operations strict() {
            return strictOperations;
        }

        @Override public Operations relaxed() {
            return relaxedOperations;
        }
    }
}
