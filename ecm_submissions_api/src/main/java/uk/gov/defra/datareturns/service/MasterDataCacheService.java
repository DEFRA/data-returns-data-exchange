package uk.gov.defra.datareturns.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.context.annotation.Scope;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.hateoas.Resource;
import org.springframework.hateoas.Resources;
import org.springframework.stereotype.Service;
import uk.gov.defra.datareturns.validation.service.MasterDataLookupService;
import uk.gov.defra.datareturns.validation.service.dto.BaseEntity;

import java.util.HashMap;
import java.util.Map;

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
            final Map<String, Long> idMap = new HashMap<>();
            final ParameterizedTypeReference<Resources<Resource<BaseEntity>>> typeRef = new
                    ParameterizedTypeReference<Resources<Resource<BaseEntity>>>() {
                    };
            masterDataLookupService.retrieve(typeRef, entityName).forEach(e -> {
                final Long id = Long.parseLong(MasterDataLookupService.getResourceId(e));
                idMap.put(e.getContent().getNomenclature(), id);
            });
            return idMap;
        }

        @Cacheable(cacheNames = "MasterDataCache:Relaxed", key = "#entityName", sync = true)
        @Override
        public Map<String, Long> getRelaxedNaturalKeyToPkMap(final String entityName) {
            log.info("Building relaxed master data cache for {} for MasterDataCacheService instance {}", entityName, this.toString());
            final Map<String, Long> idMap = new HashMap<>();
            final ParameterizedTypeReference<Resources<Resource<BaseEntity>>> typeRef = new
                    ParameterizedTypeReference<Resources<Resource<BaseEntity>>>() {
                    };
            masterDataLookupService.retrieve(typeRef, entityName).forEach(e -> {
                final Long id = Long.parseLong(MasterDataLookupService.getResourceId(e));
                idMap.put(masterDataNaturalKeyService.relaxKey(entityName, e.getContent().getNomenclature()), id);
            });

            return idMap;
        }
    }
}
