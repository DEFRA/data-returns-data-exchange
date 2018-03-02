package uk.gov.defra.datareturns.service;

import lombok.Data;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.context.annotation.Scope;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.hateoas.PagedResources;
import org.springframework.hateoas.Resource;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import uk.gov.defra.datareturns.config.ServiceEndpointConfiguration;
import uk.gov.defra.datareturns.rest.HalRestTemplate;

import java.math.BigDecimal;
import java.net.URI;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

@RequiredArgsConstructor
@Slf4j
@Service
public class ValueStandardisationService {
    private final UnitConversionFactorCache conversionFactorCache;

    public BigDecimal getStandardValue(final BigDecimal value, final String unitId) {
        if (value == null) {
            return null;
        }
        final BigDecimal factor = conversionFactorCache.getConversionFactorCache().get(unitId);
        return value.multiply(factor);
    }


    @Service
    @Scope(BeanDefinition.SCOPE_SINGLETON)
    @Slf4j
    @RequiredArgsConstructor
    private static class UnitConversionFactorCache {
        private final ServiceEndpointConfiguration services;

        @Cacheable(cacheNames = "UnitConversionFactorCache", key = "'UnitConversionFactorCache'", unless = "#result.isEmpty()")
        public Map<String, BigDecimal> getConversionFactorCache() {
            final Map<String, BigDecimal> map = new HashMap<>();
            retrieveAll().forEach(
                    e -> map.put(StringUtils.substringAfterLast(e.getId().getHref(), "/"), e.getContent().getConversion())
            );
            return map;
        }

        private Collection<Resource<UnitModel>> retrieveAll() {
            final RestTemplate restTemplate = new HalRestTemplate();
            final ServiceEndpointConfiguration.Endpoint endpoint = services.getMasterDataApi();
            endpoint.getAuth().configure(restTemplate);
            final URI target = endpoint.getUri().resolve("units");
            log.info("Fetching resource listing from {}", target);
            final ResponseEntity<PagedResources<Resource<UnitModel>>> responseEntity =
                    restTemplate.exchange(
                            target, HttpMethod.GET, null,
                            new ParameterizedTypeReference<PagedResources<Resource<UnitModel>>>() {
                            });
            if (responseEntity.getStatusCode() == HttpStatus.OK) {
                final PagedResources<Resource<UnitModel>> entity = responseEntity.getBody();
                return entity.getContent();
            }
            return Collections.emptyList();
        }

        @Data
        private static class UnitModel {
            private BigDecimal conversion;
        }
    }
}
