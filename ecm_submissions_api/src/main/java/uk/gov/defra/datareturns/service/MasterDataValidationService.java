package uk.gov.defra.datareturns.service;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;

/**
 * Cache-backed master data lookup service
 *
 * @author Sam Gardner-Dell
 */
public interface MasterDataValidationService {
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
         * @param entityName the name of the entity being sought
         * @param naturalKey the natural key to use in the lookup
         * @return a boolean, true if the entity exists, false otherwise.
         */
        boolean exists(String entityName, String naturalKey);
//
//        /**
//         * Retrieve an entity for the given key.  This operation will determine if the key can be found in the memory cache and if found
//         * will retrieve the entity from the persistence layer.
//         *
//         * @param entityName the name of the entity being sought
//         * @param naturalKey the natural key to use in the lookup
//         * @return the entity if found, null otherwise.
//         */
//        MasterDataEntity find(String entityName, String naturalKey);
    }

    /**
     * {@link MasterDataValidationService} implementation
     *
     * @author Sam Gardner-Dell
     */
    @Service
    @Scope(BeanDefinition.SCOPE_SINGLETON)
    @RequiredArgsConstructor
    class MasterDataLookupServiceImpl implements MasterDataValidationService {
        private final MasterDataNaturalKeyService masterDataNaturalKeyService;
        private final MasterDataCacheService masterDataCacheService;
        private final Operations strictOperations = new Operations() {
            @Override
            public boolean exists(final String entityName, final String naturalKey) {
                return masterDataCacheService.getStrictNaturalKeyToPkMap(entityName).containsKey(naturalKey);
            }
//
//            @Override
//            public MasterDataEntity find(String entityName, String naturalKey) {
//                Long id = masterDataCacheService.getStrictNaturalKeyToPkMap(entityName).get(naturalKey);
//                MasterDataEntity result = null;
//                if (id != null) {
//                    MasterDataRepository<E> repository = repositoryConfiguration.getMasterDataRepository(entityName);
//                    result = repository.getOne(id);
//                }
//                return result;
//            }
        };
        private final Operations relaxedOperations = new Operations() {
            @Override
            public boolean exists(final String entityName, final String naturalKey) {
                final String relaxedKey = masterDataNaturalKeyService.relaxKey(entityName, naturalKey);
                return masterDataCacheService.getRelaxedNaturalKeyToPkMap(entityName).containsKey(relaxedKey);
            }

//            @Override
//            public MasterDataEntity find(String entityName, String naturalKey) {
//                String relaxedKey = masterDataNaturalKeyService.relaxKey(entityName, naturalKey);
//                Long id = masterDataCacheService.getRelaxedNaturalKeyToPkMap(entityName).get(relaxedKey);
//                MasterDataEntity result = null;
//                if (id != null) {
//                    MasterDataRepository<E> repository = repositoryConfiguration.getMasterDataRepository(entityName);
//                    result = repository.getOne(id);
//                }
//                return result;
//            }
        };

        @Override
        public Operations strict() {
            return strictOperations;
        }

        @Override
        public Operations relaxed() {
            return relaxedOperations;
        }
    }
}
