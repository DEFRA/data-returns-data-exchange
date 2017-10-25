package uk.gov.ea.datareturns.domain.jpa.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.context.ApplicationListener;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;
import uk.gov.ea.datareturns.config.JpaRepositoryConfiguration;
import uk.gov.ea.datareturns.domain.jpa.entities.masterdata.MasterDataEntity;
import uk.gov.ea.datareturns.domain.jpa.repositories.events.MasterDataUpdateEvent;
import uk.gov.ea.datareturns.domain.jpa.repositories.masterdata.MasterDataRepository;

import javax.inject.Inject;
import java.util.HashMap;
import java.util.Map;

public interface MasterDataCacheService extends ApplicationListener<MasterDataUpdateEvent<?>> {

    Map<String, Long> getStrictNaturalKeyToPkMap(Class<? extends MasterDataEntity> entityClass);

    Map<String, Long> getRelaxedNaturalKeyToPkMap(Class<? extends MasterDataEntity> entityClass);

    @Service
    @Scope(BeanDefinition.SCOPE_SINGLETON)
    class MasterDataCacheServiceImpl implements MasterDataCacheService {
        private static final Logger LOGGER = LoggerFactory.getLogger(MasterDataCacheServiceImpl.class);
        private final JpaRepositoryConfiguration repositoryConfiguration;
        private final MasterDataNaturalKeyService masterDataNaturalKeyService;

        @Inject
        public MasterDataCacheServiceImpl(JpaRepositoryConfiguration repositoryConfiguration,
                MasterDataNaturalKeyService masterDataNaturalKeyService) {
            LOGGER.info("Creating master data cache service");
            this.repositoryConfiguration = repositoryConfiguration;
            this.masterDataNaturalKeyService = masterDataNaturalKeyService;
        }

        @Cacheable(cacheNames = "MasterDataCache:Strict", key = "#entityClass.name", sync = true)
        @Override public Map<String, Long> getStrictNaturalKeyToPkMap(Class<? extends MasterDataEntity> entityClass) {
            LOGGER.info("Building strict master data cache for " + entityClass.getName());
            MasterDataRepository<? extends MasterDataEntity> repository = repositoryConfiguration.getMasterDataRepository(entityClass);
            Map<String, Long> idMap = new HashMap<>();
            repository.findAll().forEach(e -> idMap.put(e.getName(), e.getId()));
            return idMap;
        }

        @Cacheable(cacheNames = "MasterDataCache:Relaxed", key = "#entityClass.name", sync = true)
        @Override public Map<String, Long> getRelaxedNaturalKeyToPkMap(Class<? extends MasterDataEntity> entityClass) {
            LOGGER.info("Building relaxed master data cache for " + entityClass.getName());
            MasterDataRepository<? extends MasterDataEntity> repository = repositoryConfiguration.getMasterDataRepository(entityClass);
            Map<String, Long> idMap = new HashMap<>();
            repository.findAll().forEach(e -> idMap.put(masterDataNaturalKeyService.relaxKey(e), e.getId()));
            return idMap;
        }

        @CacheEvict(cacheNames = { "MasterDataCache:Strict", "MasterDataCache:Relaxed" }, allEntries = true)
        @Override public void onApplicationEvent(MasterDataUpdateEvent<?> event) {
            LOGGER.debug("Evicting master data cache for " + event.getEntityClass().getName());
        }
    }
}