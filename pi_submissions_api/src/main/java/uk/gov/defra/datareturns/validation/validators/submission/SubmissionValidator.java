package uk.gov.defra.datareturns.validation.validators.submission;

import lombok.Data;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.hateoas.PagedResources;
import org.springframework.hateoas.Resource;
import org.springframework.hateoas.mvc.TypeReferences;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.method.P;
import org.springframework.web.client.RestTemplate;
import uk.gov.defra.datareturns.config.ServiceEndpointConfiguration;
import uk.gov.defra.datareturns.data.model.releases.Release;
import uk.gov.defra.datareturns.data.model.submissions.Submission;
import uk.gov.defra.datareturns.rest.HalRestTemplate;
import uk.gov.defra.datareturns.validation.service.ValidationCacheService;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.net.URI;
import java.time.Instant;
import java.util.Calendar;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * Validate a submission object
 *
 * @author Sam Gardner-Dell
 */
@RequiredArgsConstructor
@Slf4j
public class SubmissionValidator implements ConstraintValidator<ValidSubmission, Submission> {
    private final ServiceEndpointConfiguration services;


    @Override
    public void initialize(final ValidSubmission constraintAnnotation) {
    }

    @Override
    public boolean isValid(final Submission submission, final ConstraintValidatorContext context) {

//        validateSubmissionYear(submission);
//
//        Collection<Resource<RegimeData>> regimes = getRegimeForUniqueIdentifier(submission.getReportingReference());
//        if (regimes.size() == 1) {
//            Resource<RegimeData> regime = regimes.iterator().next();
//
//            String obligationsLink = regime.getLink("regimeObligations").getHref();
//        } else {
//            throw new RuntimeException("Unable to determine the reporting regime for the submission identifier.");
//        }
//
//
//        for (Release release : submission.getReleasesData()) {
//
//        }
        return true;
    }

    private boolean validateReportingReference(Submission submission) {
        Object reference = submission.getReportingReference();

//        http://localhost:9020/api/regimes/search/findRegimesForContextAndUniqueIdentifier?context=PI&id=uniqueIdentifiers/7

        return true;
    }

    private boolean validateSubmissionYear(Submission submission) {
        int currentYear = Calendar.getInstance().get(Calendar.YEAR);
        int oldestAllowed = currentYear - 50;

        if (submission.getApplicableYear() > currentYear || submission.getApplicableYear() < oldestAllowed) {
            throw new RuntimeException("Submission year must be between " + oldestAllowed + " and " + currentYear);
        }
        return true;
    }

    @Cacheable(cacheNames = "RegimeCache", key = "#collectionResource", unless = "#result.isEmpty()")
    public Collection<Resource<RegimeData>> getRegimeForUniqueIdentifier(final Object uniqueIdentifier) {
        String resource = "regimes/search/findRegimesForContextAndUniqueIdentifier?context=PI&id=uniqueIdentifiers/"
                + Objects.toString(uniqueIdentifier);
        PagedResources<Resource<RegimeData>> results = retrieveAll(new TypeReferences.PagedResourcesType<Resource<RegimeData>>(), resource);
        return results.getContent();
    }

    private <T> List<T> unwrap(PagedResources<Resource<T>> pagedResources) {
        return pagedResources.getContent().stream().map(Resource::getContent).collect(Collectors.toList());
    }

    private <T> PagedResources<T> retrieveAll(final TypeReferences.PagedResourcesType<T> typeReference, final String collectionResource) {
        final RestTemplate restTemplate = new HalRestTemplate();
        final ServiceEndpointConfiguration.Endpoint endpoint = services.getMasterDataApi();
        endpoint.getAuth().configure(restTemplate);
        final URI target = endpoint.getUri().resolve(collectionResource);
        log.info("Fetching resource listing from {}", target);
        final ResponseEntity<PagedResources<T>> responseEntity = restTemplate.exchange(
                target, HttpMethod.GET, null, typeReference);
        if (responseEntity.getStatusCode().is2xxSuccessful()) {
            return responseEntity.getBody();
        }
        return new PagedResources<>(Collections.emptyList(), null);
    }

    @Data
    private static class RegimeData {
        private String nomenclature;
        private String context;
    }
}
