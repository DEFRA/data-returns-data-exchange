package uk.gov.defra.datareturns.validation.validators.releases;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.hateoas.Link;
import uk.gov.defra.datareturns.data.Context;
import uk.gov.defra.datareturns.data.model.releases.Release;
import uk.gov.defra.datareturns.validation.service.MasterDataLookupService;
import uk.gov.defra.datareturns.validation.service.ValidationCacheService;
import uk.gov.defra.datareturns.validation.service.dto.Regime;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import javax.validation.ValidationException;
import java.util.List;
import java.util.Objects;
import java.util.Set;

/**
 * Validate a release within a submission
 *
 * @author Sam Gardner-Dell
 */
@RequiredArgsConstructor
@Slf4j
public class ReleaseValidator implements ConstraintValidator<ValidRelease, Release> {
    private final MasterDataLookupService lookupService;
    private final ValidationCacheService cacheService;


    @Override
    public void initialize(final ValidRelease constraintAnnotation) {
    }

    @Override
    public boolean isValid(final Release release, final ConstraintValidatorContext context) {
        boolean valid = true;

        final Object reportingReference = release.getSubmission().getReportingReference();
        String resource = "regimes/search/findRegimesForContextAndUniqueIdentifier?";
        resource += "context=" + Context.PI.name();
        resource += "&id=uniqueIdentifiers/" + Objects.toString(reportingReference);
        final Link regimesLookup = new Link(resource);
        final List<Regime> regimes = lookupService.list(Regime.class, regimesLookup);

        if (regimes.size() == 1) {
            final Regime regime = regimes.get(0);
            final Set<String> parametersForRoute = cacheService.getRouteParameterMapForRegime(regime).get("" + release.getRouteId());
            if (parametersForRoute == null || !parametersForRoute.contains("" + release.getSubstanceId())) {
                context.disableDefaultConstraintViolation();
                context.buildConstraintViolationWithTemplate("INVALID_RELEASE_SUBSTANCE")
                        .addPropertyNode("substanceId").addConstraintViolation();
                valid = false;
            }
        } else {
            throw new ValidationException("Pollution inventory submissions should be mapped to a single pollution inventory regime.");
        }
        return valid;
    }
}
