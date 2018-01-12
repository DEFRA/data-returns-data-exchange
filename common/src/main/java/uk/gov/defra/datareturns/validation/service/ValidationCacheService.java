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
import org.springframework.http.client.support.BasicAuthorizationInterceptor;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import uk.gov.defra.datareturns.rest.HalRestTemplate;

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public interface ValidationCacheService {


    /**
     * Get a map from ID to nomenclature for validation
     *
     * @param resourceCollectionUri
     * @return a {@link Map} from ID to nomenclature
     */
    Map<String, String> getResourceNomenclatureMap(final String resourceCollectionUri);


    @Service
    @Scope(BeanDefinition.SCOPE_SINGLETON)
    @Slf4j
    @RequiredArgsConstructor
    class ValidationCacheServiceImpl implements ValidationCacheService {

        @Cacheable(cacheNames = "ValidationCache", key = "#resourceCollectionUri", unless = "#result.isEmpty()")
        @Override
        public Map<String, String> getResourceNomenclatureMap(final String resourceCollectionUri) {
            log.info("Fetching resource listing from {}", resourceCollectionUri);
            final Map<String, String> map = new HashMap<>();
            retrieveAll(resourceCollectionUri).forEach(
                    e -> map.put(StringUtils.substringAfterLast(e.getId().getHref(), "/"), e.getContent().getNomenclature())
            );
            return map;
        }

        private Collection<Resource<RestResultEntry>> retrieveAll(final String resourceCollectionUri) {
            final RestTemplate restTemplate = new HalRestTemplate();
            // FIXME: Pass through authentication?
            restTemplate.getInterceptors().add(new BasicAuthorizationInterceptor("user", "password"));
            // FIXME: URL should not be hardcoded
            final ResponseEntity<PagedResources<Resource<RestResultEntry>>> responseEntity =
                    restTemplate.exchange("http://localhost:9020/api/" + resourceCollectionUri,
                            HttpMethod.GET, null,
                            new ParameterizedTypeReference<PagedResources<Resource<RestResultEntry>>>() {
                            },
                            Collections.emptyMap());
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
