package uk.gov.defra.datareturns.validation.service;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.context.annotation.Scope;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.hateoas.Identifiable;
import org.springframework.hateoas.Link;
import org.springframework.hateoas.Resources;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;
import uk.gov.defra.datareturns.config.ServiceEndpointConfiguration;
import uk.gov.defra.datareturns.rest.HalRestTemplate;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.net.URI;
import java.util.ArrayList;
import java.util.List;

/**
 * {@link MasterDataLookupService} provides a simple interface to lookup HAL compliant data from the master data API.
 *
 * @author Sam Gardner-Dell
 */
public interface MasterDataLookupService {
    /**
     * Retrieve the resource identifier from the URI of the resource (minus the base url)
     *
     * @param resource the resource representing the entity whose identifier should be returned
     * @return the identifier
     */
    static String getResourceId(final Identifiable<Link> resource) {
        return StringUtils.substringAfterLast(resource.getId().expand().getHref(), "/");
    }

    /**
     * Retrieve a single entity by requesting the linked entity resource
     *
     * @param cls            the entity class used to deserialize the response
     * @param entityResource the linked entity resource
     * @param <T>            the generic type of the class used to deserialize the response
     * @return an {@link OptionalResult} backed by a the deserialized response as an instance of {@link T}
     */
    <T> OptionalResult<T> get(Class<T> cls, final Link entityResource);

    /**
     * Retrieve a list of entities by requesting the linked collection resource
     *
     * @param cls                the entity class used to deserialize entities within the response
     * @param collectionResource the linked collection resource
     * @param <T>                the generic type of the class used to deserialize entities within the response
     * @return an {@link OptionalResult} backed by a {@link List<T>} containing all entities that were deserialized from the response
     */
    <T> OptionalResult<List<T>> list(Class<T> cls, final Link collectionResource);


    /**
     * Default service implementation for the {@link MasterDataLookupService}
     *
     * @author Sam Gardner-Dell
     */
    @Service
    @Scope(BeanDefinition.SCOPE_SINGLETON)
    @Slf4j
    @RequiredArgsConstructor
    class MasterDataLookupServiceImpl implements MasterDataLookupService {
        private final ServiceEndpointConfiguration services;

        @Cacheable(cacheNames = "MasterDataCache", key = "#cls.name + ':Entities:' + #entityResource.href.replaceAll(\":\", \"\")")
        @Override
        public <T> OptionalResult<T> get(final Class<T> cls, final Link entityResource) {
            final RequestFacade request = new RequestFacade(entityResource);
            return OptionalResult.of(() -> request.exchange(HttpMethod.GET, null, cls));
        }

        @Cacheable(cacheNames = "MasterDataCache", key = "#cls.name + ':Collections:' + #collectionResource.href.replaceAll(\":\", \"\")")
        @Override
        public <T> OptionalResult<List<T>> list(final Class<T> cls, final Link collectionResource) {
            final ParameterizedTypeReference<Resources<T>> typeReference = new ParameterizedTypeReference<Resources<T>>() {
                @Override
                public Type getType() {
                    return new ExplicitParameterizedType((ParameterizedType) super.getType(), new Type[] {cls});
                }
            };
            final RequestFacade request = new RequestFacade(collectionResource);
            return OptionalResult.of(
                    () -> request.exchange(HttpMethod.GET, null, typeReference),
                    (r) -> new ArrayList<>(r.getBody().getContent()));
        }

        /**
         * The {@link ExplicitParameterizedType} implementation of {@link ParameterizedType} for use with {@link ParameterizedTypeReference} to
         * enable the use of generics.
         *
         * @author Sam Gardner-Dell
         */
        private static final class ExplicitParameterizedType implements ParameterizedType {
            private final ParameterizedType delegate;
            private final Type[] actualTypeArguments;

            /**
             * Create a new {@link ExplicitParameterizedType}
             *
             * @param delegate            the delegate {@link ParameterizedType} instance
             * @param actualTypeArguments the actual type arguments array (should contain the class reference for the generic type to be used)
             */
            private ExplicitParameterizedType(final ParameterizedType delegate, final Type[] actualTypeArguments) {
                this.delegate = delegate;
                this.actualTypeArguments = actualTypeArguments;
            }

            @Override
            public Type[] getActualTypeArguments() {
                return actualTypeArguments;
            }

            @Override
            public Type getRawType() {
                return delegate.getRawType();
            }

            @Override
            public Type getOwnerType() {
                return delegate.getOwnerType();
            }
        }

        /**
         * Simple request facade
         */
        private final class RequestFacade {
            private final RestTemplate template;
            @Getter
            private final URI target;

            /**
             * Create a new request facade for the specified resource.
             *
             * @param resource a {@link Link} object describing the resource to which a request may be made
             */
            private RequestFacade(final Link resource) {
                this.template = new HalRestTemplate();
                final ServiceEndpointConfiguration.Endpoint endpoint = services.getMasterDataApi();
                endpoint.getAuth().configure(this.template);
                this.target = endpoint.getUri().resolve(resource.getHref());
            }


            /**
             * Make a request via the delgate {@link RestTemplate}
             *
             * @param method        the {@link HttpMethod} for the request
             * @param requestEntity the request entity
             * @param responseType  a {@link ParameterizedTypeReference} describing the expected response type
             * @param <T>           the generic type for the response content
             * @return a {@link ResponseEntity} instance for the generic type
             * @throws RestClientException if an issue occurs making the request
             */
            public <T> ResponseEntity<T> exchange(final HttpMethod method, final HttpEntity<?> requestEntity,
                                                  final ParameterizedTypeReference<T> responseType)
                    throws RestClientException {
                return logged(template.exchange(target, method, requestEntity, responseType));
            }

            /**
             * Make a request via the delgate {@link RestTemplate}
             *
             * @param method        the {@link HttpMethod} for the request
             * @param requestEntity the request entity
             * @param responseType  a {@link Class} providing the expected response type
             * @param <T>           the generic type for the response content
             * @return a {@link ResponseEntity} instance for the generic type
             * @throws RestClientException if an issue occurs making the request
             */
            public <T> ResponseEntity<T> exchange(final HttpMethod method, final HttpEntity<?> requestEntity, final Class<T> responseType)
                    throws RestClientException {
                return logged(template.exchange(target, method, requestEntity, responseType));
            }

            /**
             * Helper method to log the response from a request
             *
             * @param response the response to be logged
             * @param <T>      the generic type of the response content
             * @return the (unchanged) response
             */
            private <T> ResponseEntity<T> logged(final ResponseEntity<T> response) {
                log.info("GET {} {}", response.getStatusCode(), getTarget());
                return response;
            }
        }
    }
}
