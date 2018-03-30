package uk.gov.defra.datareturns.validation.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.context.annotation.Scope;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.hateoas.Resource;
import org.springframework.hateoas.Resources;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import uk.gov.defra.datareturns.config.ServiceEndpointConfiguration;
import uk.gov.defra.datareturns.data.Context;
import uk.gov.defra.datareturns.rest.HalRestTemplate;
import uk.gov.defra.datareturns.validation.service.dto.Parameter;
import uk.gov.defra.datareturns.validation.service.dto.ParameterGroup;
import uk.gov.defra.datareturns.validation.service.dto.Regime;
import uk.gov.defra.datareturns.validation.service.dto.RegimeObligation;
import uk.gov.defra.datareturns.validation.service.dto.Route;

import javax.validation.ValidationException;
import java.net.URI;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

public interface MasterDataLookupService {
    /**
     * Retrieve master data from the given collection resource mapping data using the provided {@link ParameterizedTypeReference}
     *
     * @param typeReference the {@link ParameterizedTypeReference} used to map the result to the domain model
     * @param resource      the collection or entity resource relative to the API base
     * @param <T>           the type of the domain model
     * @return the resulting domain model from parsing the HTTP response.
     */
    <T> T retrieve(final ParameterizedTypeReference<T> typeReference, final String resource);

    Map<Context, List<Resource<Regime>>> getRegimes();

    List<Resource<Regime>> getRegimes(final Context context, final Object uniqueIdentifier);

    List<Resource<RegimeObligation>> getRegimeObligations(Resource<Regime> regime);

    Resource<Route> getRoute(final Resource<RegimeObligation> obligation);

    List<Resource<ParameterGroup>> getParameterGroups(final Resource<RegimeObligation> obligation);

    List<Resource<Parameter>> getParametersForRegimeObligation(final Resource<RegimeObligation> obligation);

    List<Resource<Parameter>> getParameters(final Resource<ParameterGroup> parameterGroup);

    /**
     * Retrieve the resource identifier from the URI of the resource (minus the base url)
     *
     * @param resource the resource representing the entity whose identifier should be returned
     * @return the identifier
     */
    static String getResourceId(final Resource resource) {
        return StringUtils.substringAfterLast(resource.getId().expand().getHref(), "/");
    }

    @Service
    @Scope(BeanDefinition.SCOPE_SINGLETON)
    @Slf4j
    @RequiredArgsConstructor
    class MasterDataLookupServiceImpl implements MasterDataLookupService {
        private final ServiceEndpointConfiguration services;

        @Override
        public Map<Context, List<Resource<Regime>>> getRegimes() {
            final ParameterizedTypeReference<Resources<Resource<Regime>>> typeRef = new
                    ParameterizedTypeReference<Resources<Resource<Regime>>>() {
                    };
            final Collection<Resource<Regime>> result = retrieve(typeRef, "regimes").getContent();
            return result.stream()
                    .collect(Collectors.groupingBy(r -> Context.valueOf(r.getContent().getContext()), Collectors.toList()));
        }


        @Override
        public List<Resource<RegimeObligation>> getRegimeObligations(final Resource<Regime> regime) {
            final ParameterizedTypeReference<Resources<Resource<RegimeObligation>>> typeRef = new
                    ParameterizedTypeReference<Resources<Resource<RegimeObligation>>>() {
                    };
            return new ArrayList<>(retrieve(typeRef, regime.getLink("regimeObligations").expand().getHref()).getContent());
        }

        @Override
        public Resource<Route> getRoute(final Resource<RegimeObligation> obligation) {
            final ParameterizedTypeReference<Resource<Route>> typeRef = new
                    ParameterizedTypeReference<Resource<Route>>() {
                    };
            return retrieve(typeRef, obligation.getLink("route").expand().getHref());
        }

        @Override
        public List<Resource<ParameterGroup>> getParameterGroups(final Resource<RegimeObligation> obligation) {
            final ParameterizedTypeReference<Resources<Resource<ParameterGroup>>> typeRef = new
                    ParameterizedTypeReference<Resources<Resource<ParameterGroup>>>() {
                    };
            return new ArrayList<>(retrieve(typeRef, obligation.getLink("parameterGroups").expand().getHref()).getContent());
        }

        @Override
        public List<Resource<Parameter>> getParametersForRegimeObligation(final Resource<RegimeObligation> obligation) {
            return getParameterGroups(obligation).stream().flatMap(pg -> getParameters(pg).stream()).collect(Collectors.toList());
        }

        @Override
        public List<Resource<Parameter>> getParameters(final Resource<ParameterGroup> parameterGroup) {
            final ParameterizedTypeReference<Resources<Resource<Parameter>>> typeRef = new
                    ParameterizedTypeReference<Resources<Resource<Parameter>>>() {
                    };
            return new ArrayList<>(retrieve(typeRef, parameterGroup.getLink("parameters").expand().getHref()).getContent());
        }

        @Override
        public List<Resource<Regime>> getRegimes(final Context context, final Object uniqueIdentifier) {
            String resource = "regimes/search/findRegimesForContextAndUniqueIdentifier?";
            resource += "context=" + context.name();
            resource += "&id=uniqueIdentifiers/" + Objects.toString(uniqueIdentifier);

            final ParameterizedTypeReference<Resources<Resource<Regime>>> typeRef = new
                    ParameterizedTypeReference<Resources<Resource<Regime>>>() {
                    };
            return new ArrayList<>(retrieve(typeRef, resource).getContent());
        }

        public <T> T retrieve(final ParameterizedTypeReference<T> typeReference, final String resource) {
            final RestTemplate restTemplate = new HalRestTemplate();
            final ServiceEndpointConfiguration.Endpoint endpoint = services.getMasterDataApi();
            endpoint.getAuth().configure(restTemplate);
            final URI target = endpoint.getUri().resolve(resource);
            final ResponseEntity<T> responseEntity = restTemplate.exchange(target, HttpMethod.GET, null, typeReference);

            log.info("GET {} {}", responseEntity.getStatusCode(), target);
            if (responseEntity.getStatusCode().is2xxSuccessful()) {
                return responseEntity.getBody();
            }
            throw new ValidationException("Unexpected response from master data API: " + responseEntity.getStatusCode());
        }
    }
}
