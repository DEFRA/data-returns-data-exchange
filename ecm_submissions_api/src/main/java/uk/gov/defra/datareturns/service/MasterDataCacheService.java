package uk.gov.defra.datareturns.service;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.context.annotation.Scope;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.hateoas.PagedResources;
import org.springframework.hateoas.Resource;
import org.springframework.hateoas.ResourceSupport;
import org.springframework.hateoas.hal.Jackson2HalModule;
import org.springframework.hateoas.mvc.TypeConstrainedMappingJackson2HttpMessageConverter;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.support.BasicAuthorizationInterceptor;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
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
        private final MasterDataNaturalKeyService masterDataNaturalKeyService;

        @Cacheable(cacheNames = "MasterDataCache:Strict", key = "#entityName", sync = true)
        @Override
        public Map<String, Long> getStrictNaturalKeyToPkMap(final String entityName) {
            log.info("Building strict master data cache for {} for MasterDataCacheService instance {}", entityName, this.toString());
            final Map<String, Long> idMap = new HashMap<>();
            retrieveAll(entityName).forEach(e -> idMap.put(e.getNomenclature(), e.getId()));
            return idMap;
        }

        @Cacheable(cacheNames = "MasterDataCache:Relaxed", key = "#entityName", sync = true)
        @Override
        public Map<String, Long> getRelaxedNaturalKeyToPkMap(final String entityName) {
            log.info("Building relaxed master data cache for {} for MasterDataCacheService instance {}", entityName, this.toString());
            final Map<String, Long> idMap = new HashMap<>();
            retrieveAll(entityName).forEach(e -> idMap.put(masterDataNaturalKeyService.relaxKey(entityName, e.getNomenclature()), e.getId()));
            return idMap;
        }

        private List<MasterDataEntity> retrieveAll(final String entityName) {
            final RestTemplate restTemplate = getRestTemplateWithHalMessageConverter();
            // FIXME: URL should not be hardcoded
            final ResponseEntity<PagedResources<Resource<MasterDataEntity>>> responseEntity =
                    restTemplate.exchange("http://localhost:9020/api/" + entityName,
                            HttpMethod.GET, null,
                            new ParameterizedTypeReference<PagedResources<Resource<MasterDataEntity>>>() {
                            },
                            Collections.emptyMap());
            if (responseEntity.getStatusCode() == HttpStatus.OK) {
                final PagedResources<Resource<MasterDataEntity>> entity = responseEntity.getBody();
                final Collection<Resource<MasterDataEntity>> resources = entity.getContent();
                return resources.stream().map(Resource::getContent).collect(Collectors.toList());
            }
            return Collections.emptyList();
        }

        private RestTemplate getRestTemplateWithHalMessageConverter() {
            final RestTemplate restTemplate = new RestTemplate(Arrays.asList(getHalMessageConverter()));
            restTemplate.getInterceptors().add(new BasicAuthorizationInterceptor("user", "password"));
            return restTemplate;
        }

        private HttpMessageConverter getHalMessageConverter() {
            final ObjectMapper objectMapper = new ObjectMapper();
            objectMapper.registerModule(new Jackson2HalModule());
            objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
            final MappingJackson2HttpMessageConverter halConverter = new TypeConstrainedMappingJackson2HttpMessageConverter(ResourceSupport.class);
            halConverter.setSupportedMediaTypes(MediaType.parseMediaTypes("application/hal+json"));
            halConverter.setObjectMapper(objectMapper);
            return halConverter;
        }
    }
}
