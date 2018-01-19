package uk.gov.defra.datareturns.validation.service;


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

import java.net.URI;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

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

    @Service
    @Scope(BeanDefinition.SCOPE_SINGLETON)
    @Slf4j
    @RequiredArgsConstructor
    class ValidationCacheServiceImpl implements ValidationCacheService {
        private final ServiceEndpointConfiguration services;

        @Cacheable(cacheNames = "ValidationCache", key = "#collectionResource", unless = "#result.isEmpty()")
        @Override
        public Map<String, String> getResourceNomenclatureMap(final String collectionResource) {
            final Map<String, String> map = new HashMap<>();
            retrieveAll(collectionResource).forEach(
                    e -> map.put(StringUtils.substringAfterLast(e.getId().getHref(), "/"), e.getContent().getNomenclature())
            );
            return map;
        }

        private Collection<Resource<RestResultEntry>> retrieveAll(final String collectionResource) {
            final RestTemplate restTemplate = new HalRestTemplate();
            final ServiceEndpointConfiguration.Endpoint endpoint = services.getEndpoint(ServiceEndpointConfiguration.MASTER_DATA_API);
            endpoint.getAuth().configure(restTemplate);
            final URI target = endpoint.getUri().resolve(collectionResource);
            log.info("Fetching resource listing from {}", target);
            final ResponseEntity<PagedResources<Resource<RestResultEntry>>> responseEntity =
                    restTemplate.exchange(
                            target, HttpMethod.GET, null,
                            new ParameterizedTypeReference<PagedResources<Resource<RestResultEntry>>>() {
                            });
            if (responseEntity.getStatusCode() == HttpStatus.OK) {
                final PagedResources<Resource<RestResultEntry>> entity = responseEntity.getBody();
                return entity.getContent();
            }
            return Collections.emptyList();
        }

        @Data
        private static class RestResultEntry {
            private String nomenclature;
        }
    }
}
