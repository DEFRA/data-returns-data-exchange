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
import uk.gov.defra.datareturns.validation.service.dto.MdUnit;

import javax.annotation.PostConstruct;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

import static uk.gov.defra.datareturns.validation.service.MasterDataEntity.PARAMETER;
import static uk.gov.defra.datareturns.validation.service.MasterDataEntity.PARAMETER_GROUP;
import static uk.gov.defra.datareturns.validation.service.MasterDataEntity.REGIME_OBLIGATION;
import static uk.gov.defra.datareturns.validation.service.MasterDataEntity.ROUTE;
import static uk.gov.defra.datareturns.validation.service.MasterDataEntity.UNIT;

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

    Map<String, Set<String>> getParametersByRoute(MdRegime regime);

    Map<String, Set<String>> getUnitsByRoute(MdRegime regime);

    Map<String, MdRegimeObligation> getObligationsByRouteId(MdRegime regime);

    /**
     * Validation cache service implementation
     */
    @Service
    @Scope(BeanDefinition.SCOPE_SINGLETON)
    @Slf4j
    @RequiredArgsConstructor
    class ValidationCacheServiceImpl implements ValidationCacheService {
        private final MasterDataLookupService lookupService;
        private ValidationCacheService proxy;

        @PostConstruct
        public void init() {
            this.proxy = this;
        }


        @Cacheable(cacheNames = "ValidationCache", key = "#collectionResource", unless = "#result.isEmpty()")
        @Override
        public Map<String, String> getResourceNomenclatureMap(final String collectionResource) {
            final List<MdBaseEntity> result = lookupService.list(MdBaseEntity.class, new Link(collectionResource)).orThrow();
            return result.stream().collect(Collectors.toMap(MasterDataLookupService::getResourceId, MdBaseEntity::getNomenclature));
        }


        @Cacheable(cacheNames = "ValidationCache:Regime",
                   key = "T(uk.gov.defra.datareturns.validation.service.MasterDataLookupService).getResourceId(#regime) + ':ParametersByRoute'",
                   unless = "#result.isEmpty()")
        public Map<String, Set<String>> getParametersByRoute(final MdRegime regime) {
            final Map<String, MdRegimeObligation> obligationsByRouteId = proxy.getObligationsByRouteId(regime);

            final Map<String, Set<String>> parametersByRouteId = obligationsByRouteId.entrySet().stream()
                    .collect(
                            Collectors.toMap(
                                    Map.Entry::getKey,
                                    e -> {
                                        final List<MdParameterGroup> parameterGroups = lookupService
                                                .list(MdParameterGroup.class, e.getValue().getCollectionLink(PARAMETER_GROUP)).orThrow();
                                        final List<MdParameter> parameters = parameterGroups.stream().flatMap(
                                                pg -> lookupService.list(MdParameter.class, pg.getCollectionLink(PARAMETER)).orThrow().stream()
                                        ).collect(Collectors.toList());
                                        return parameters.stream().map(MasterDataLookupService::getResourceId).collect(Collectors.toSet());
                                    }
                            )
                    );
            return parametersByRouteId;
        }


        @Cacheable(cacheNames = "ValidationCache:Regime",
                   key = "T(uk.gov.defra.datareturns.validation.service.MasterDataLookupService).getResourceId(#regime) + ':UnitsByRoute'",
                   unless = "#result.isEmpty()")
        public Map<String, Set<String>> getUnitsByRoute(final MdRegime regime) {
            final Map<String, MdRegimeObligation> obligationsByRouteId = proxy.getObligationsByRouteId(regime);

            final Map<String, Set<String>> parametersByRouteId = obligationsByRouteId.entrySet().stream()
                    .collect(
                            Collectors.toMap(
                                    Map.Entry::getKey,
                                    e -> {
                                        final List<MdUnit> units = lookupService.list(MdUnit.class, e.getValue().getCollectionLink(UNIT)).orThrow();
                                        return units.stream().map(MasterDataLookupService::getResourceId).collect(Collectors.toSet());
                                    }
                            )
                    );
            return parametersByRouteId;
        }

        @Cacheable(cacheNames = "ValidationCache:Regime",
                   key = "T(uk.gov.defra.datareturns.validation.service.MasterDataLookupService).getResourceId(#regime) + ':ObligationsByRoute'",
                   unless = "#result.isEmpty()")
        public Map<String, MdRegimeObligation> getObligationsByRouteId(final MdRegime regime) {
            final List<MdRegimeObligation> obligations = lookupService.list(MdRegimeObligation.class, regime.getCollectionLink(REGIME_OBLIGATION))
                    .orThrow();
            final Map<String, MdRegimeObligation> obligationsByRouteId = obligations.stream()
                    .collect(
                            Collectors.toMap(
                                    o -> MasterDataLookupService.getResourceId(lookupService.get(MdRoute.class, o.getItemLink(ROUTE)).orThrow()),
                                    Function.identity()
                            )
                    );
            return obligationsByRouteId;
        }
    }
}
