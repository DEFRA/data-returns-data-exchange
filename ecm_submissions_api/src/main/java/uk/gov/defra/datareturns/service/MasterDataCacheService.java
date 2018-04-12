package uk.gov.defra.datareturns.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.context.annotation.Scope;
import org.springframework.hateoas.Link;
import org.springframework.stereotype.Service;
import uk.gov.defra.datareturns.validation.service.MasterDataLookupService;
import uk.gov.defra.datareturns.validation.service.dto.BaseEntity;

import java.util.Map;
import java.util.stream.Collectors;

public interface MasterDataCacheService {
    Map<String, Long> getStrictNaturalKeyToPkMap(String entityName);

    Map<String, Long> getRelaxedNaturalKeyToPkMap(String entityName);

    @Service
    @Scope(BeanDefinition.SCOPE_SINGLETON)
    @Slf4j
    @RequiredArgsConstructor
    class MasterDataCacheServiceImpl implements MasterDataCacheService {
        private final MasterDataLookupService masterDataLookupService;
        private final MasterDataNaturalKeyService masterDataNaturalKeyService;

        @Cacheable(cacheNames = "MasterDataCache:Strict", key = "#entityName", sync = true)
        @Override
        public Map<String, Long> getStrictNaturalKeyToPkMap(final String entityName) {
            log.info("Building strict master data cache for {} for MasterDataCacheService instance {}", entityName, this.toString());
            return masterDataLookupService.list(BaseEntity.class, new Link(entityName)).stream()
                    .collect(Collectors.toMap(
                            BaseEntity::getNomenclature,
                            e -> Long.parseLong(MasterDataLookupService.getResourceId(e)))
                    );
        }

        @Cacheable(cacheNames = "MasterDataCache:Relaxed", key = "#entityName", sync = true)
        @Override
        public Map<String, Long> getRelaxedNaturalKeyToPkMap(final String entityName) {
            log.info("Building relaxed master data cache for {} for MasterDataCacheService instance {}", entityName, this.toString());
            return masterDataLookupService.list(BaseEntity.class, new Link(entityName)).stream()
                    .collect(Collectors.toMap(
                            e -> masterDataNaturalKeyService.relaxKey(entityName, e.getNomenclature()),
                            e -> Long.parseLong(MasterDataLookupService.getResourceId(e)))
                    );
        }
    }
}
