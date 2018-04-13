package uk.gov.defra.datareturns.validation.service;


import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.context.annotation.Scope;
import org.springframework.hateoas.Link;
import org.springframework.stereotype.Service;
import uk.gov.defra.datareturns.validation.service.dto.MdBaseEntity;
import uk.gov.defra.datareturns.validation.service.dto.MdParameter;
import uk.gov.defra.datareturns.validation.service.dto.MdParameterGroup;
import uk.gov.defra.datareturns.validation.service.dto.MdRegime;
import uk.gov.defra.datareturns.validation.service.dto.MdRegimeObligation;
import uk.gov.defra.datareturns.validation.service.dto.MdRoute;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * Provides caching of master data via the spring cache framework for use in validation.
 *
 * @author Sam Gardner-Dell
 */
public interface ValidationCacheService {
    /**
     * Get a map from ID to nomenclature for validation
     *
     * @param collectionResource the collection resource to lookup allowable values
     * @return a {@link Map} from ID to nomenclature
     */
    Map<String, String> getResourceNomenclatureMap(final String collectionResource);

    Map<String, Set<String>> getRouteParameterMapForRegime(MdRegime regime);

    /**
     * Validation cache service implementation
     */
    @Service
    @Scope(BeanDefinition.SCOPE_SINGLETON)
    @Slf4j
    @RequiredArgsConstructor
    class ValidationCacheServiceImpl implements ValidationCacheService {
        private final MasterDataLookupService lookupService;

        @Cacheable(cacheNames = "ValidationCache", key = "#collectionResource", unless = "#result.isEmpty()")
        @Override
        public Map<String, String> getResourceNomenclatureMap(final String collectionResource) {
            final List<MdBaseEntity> result = lookupService.list(MdBaseEntity.class, new Link(collectionResource));
            return result.stream().collect(Collectors.toMap(MasterDataLookupService::getResourceId, MdBaseEntity::getNomenclature));
        }


        @Cacheable(cacheNames = "ValidationCache:Regime",
                   key = "T(uk.gov.defra.datareturns.validation.service.MasterDataLookupService).getResourceId(#regime) + ':ParametersByRoute'",
                   unless = "#result.isEmpty()")
        public Map<String, Set<String>> getRouteParameterMapForRegime(final MdRegime regime) {
            final List<MdRegimeObligation> obligations = lookupService.list(MdRegimeObligation.class, regime.getLink("regimeObligations"));
            final Map<String, MdRegimeObligation> obligationsByRouteId = obligations.stream()
                    .collect(
                            Collectors.toMap(
                                    o -> MasterDataLookupService.getResourceId(lookupService.get(MdRoute.class, o.getLink("route"))),
                                    Function.identity()
                            )
                    );

            final Map<String, Set<String>> parametersByRouteId = obligationsByRouteId.entrySet().stream()
                    .collect(
                            Collectors.toMap(
                                    Map.Entry::getKey,
                                    e -> {
                                        final List<MdParameterGroup> parameterGroups = lookupService
                                                .list(MdParameterGroup.class, e.getValue().getLink("parameterGroups"));
                                        final List<MdParameter> parameters = parameterGroups.stream().flatMap(
                                                pg -> lookupService.list(MdParameter.class, pg.getLink("parameters")).stream()
                                        ).collect(Collectors.toList());
                                        return parameters.stream().map(MasterDataLookupService::getResourceId).collect(Collectors.toSet());
                                    }
                            )
                    );
            return parametersByRouteId;
        }
    }
}
