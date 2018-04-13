package uk.gov.defra.datareturns.validation.validators.releases;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.hateoas.Link;
import uk.gov.defra.datareturns.data.Context;
import uk.gov.defra.datareturns.data.model.releases.Release;
import uk.gov.defra.datareturns.service.ValueStandardisationService;
import uk.gov.defra.datareturns.validation.service.MasterDataLookupService;
import uk.gov.defra.datareturns.validation.service.ValidationCacheService;
import uk.gov.defra.datareturns.validation.service.dto.MdRegime;
import uk.gov.defra.datareturns.validation.service.dto.MdSubroute;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import javax.validation.ValidationException;
import java.math.BigDecimal;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

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
    private final ValueStandardisationService valueStandardisationService;


    @Override
    public void initialize(final ValidRelease constraintAnnotation) {
    }

    @Override
    public boolean isValid(final Release release, final ConstraintValidatorContext context) {
        boolean valid = checkRequiredFields(release, context);
        valid = checkValidReleaseSubstance(release, context) && valid;
        valid = checkValidSubrouteForRoute(release, context) && valid;
        valid = checkValidReleaseUnit(release, context) && valid;
        valid = checkValidNotifiableRelease(release, context) && valid;
        valid = checkNotifiableReleaseLessThanTotalRelease(release, context) && valid;
        return valid;
    }


    private boolean checkRequiredFields(final Release release, final ConstraintValidatorContext context) {
        boolean valid = true;
        if (release.getSubstanceId() == null) {
            context.disableDefaultConstraintViolation();
            context.buildConstraintViolationWithTemplate("RELEASE_SUBSTANCE_NOT_SPECIFIED").addPropertyNode("substanceId").addConstraintViolation();
            valid = false;
        }
        if (release.getRouteId() == null) {
            context.disableDefaultConstraintViolation();
            context.buildConstraintViolationWithTemplate("RELEASE_ROUTE_NOT_SPECIFIED").addPropertyNode("routeId").addConstraintViolation();
            valid = false;
        }
        if (release.getMethod() == null) {
            context.disableDefaultConstraintViolation();
            context.buildConstraintViolationWithTemplate("RELEASE_METHOD_NOT_SPECIFIED").addPropertyNode("routeId").addConstraintViolation();
            valid = false;
        }
        return valid;
    }

    private boolean checkValidReleaseSubstance(final Release release, final ConstraintValidatorContext context) {
        boolean valid = true;
        if (release.getSubstanceId() != null) {
            final Object reportingReference = release.getSubmission().getReportingReference();

            String resource = "regimes/search/findRegimesForContextAndUniqueIdentifier?";
            resource += "context=" + Context.PI.name();
            resource += "&id=uniqueIdentifiers/" + Objects.toString(reportingReference);
            final Link regimesLookup = new Link(resource);
            final List<MdRegime> regimes = lookupService.list(MdRegime.class, regimesLookup);

            if (regimes.size() == 1) {
                final MdRegime regime = regimes.get(0);
                final Set<String> parametersForRoute = cacheService.getRouteParameterMapForRegime(regime).get("" + release.getRouteId());
                if (parametersForRoute == null || !parametersForRoute.contains("" + release.getSubstanceId())) {
                    context.disableDefaultConstraintViolation();
                    context.buildConstraintViolationWithTemplate("RELEASE_SUBSTANCE_INVALID")
                            .addPropertyNode("substanceId").addConstraintViolation();
                    valid = false;
                }
            } else {
                throw new ValidationException("Pollution inventory submissions should be mapped to a single pollution inventory regime.");
            }
        }
        return valid;
    }

    private boolean checkValidReleaseUnit(final Release release, final ConstraintValidatorContext context) {
        boolean valid = true;
        // Must have a valid unit if value is not below the reporting threshold
        if (!Boolean.TRUE.equals(release.getBelowReportingThreshold())) {
            if (release.getUnitId() == null) {
                context.disableDefaultConstraintViolation();
                context.buildConstraintViolationWithTemplate("RELEASE_UNIT_NOT_SPECIFIED")
                        .addPropertyNode("unitId").addConstraintViolation();
                valid = false;
//            } else {
                // FIXME: Need to do lookup against master data API to determine if valid unit for regime

            }
        }
        return valid;
    }


    private boolean checkValidNotifiableRelease(final Release release, final ConstraintValidatorContext context) {
        boolean valid = true;

        if (release.getNotifiableValue() != null) {
            // Must have a valid notifiable release unit value
            if (release.getNotifiableUnitId() == null) {
                context.disableDefaultConstraintViolation();
                context.buildConstraintViolationWithTemplate("RELEASE_NOTIFIABLE_UNIT_NOT_SPECIFIED")
                        .addPropertyNode("notifiableUnitId").addConstraintViolation();
                valid = false;
//            } else {
                // FIXME: Need to do lookup against master data API to determine if valid unit for regime
            }

            // Must have a reason for a notifiable release
            if (StringUtils.isEmpty(release.getNotifiableReason())) {
                context.disableDefaultConstraintViolation();
                context.buildConstraintViolationWithTemplate("RELEASE_NOTIFIABLE_REASON_NOT_SPECIFIED")
                        .addPropertyNode("notifiableReason").addConstraintViolation();
                valid = false;
            }
        }
        return valid;
    }

    private boolean checkNotifiableReleaseLessThanTotalRelease(final Release release, final ConstraintValidatorContext context) {
        boolean valid = true;

        if (release.getNotifiableValue() != null) {
            if (release.getNotifiableUnitId() != null) {
                final BigDecimal totalValue = valueStandardisationService.getStandardValue(release.getValue(), String.valueOf(release.getUnitId()));
                final BigDecimal notifiableValue = valueStandardisationService.getStandardValue(release.getNotifiableValue(),
                        String.valueOf(release.getNotifiableUnitId()));

                if (notifiableValue.compareTo(totalValue) > 0) {
                    context.disableDefaultConstraintViolation();
                    context.buildConstraintViolationWithTemplate("RELEASE_NOTIFIABLE_VALUE_EXCEEDS_TOTAL")
                            .addPropertyNode("subrouteId").addConstraintViolation();
                    valid = false;
                }

            }
        }
        return valid;
    }


    private boolean checkValidSubrouteForRoute(final Release release, final ConstraintValidatorContext context) {
        boolean valid = true;
        if (release.getRouteId() != null) {
            final List<MdSubroute> subroutes = lookupService.list(MdSubroute.class, new Link("routes/" + release.getRouteId() + "/subroutes"));
            final List<String> subrouteIds = subroutes.stream().map(MasterDataLookupService::getResourceId).collect(Collectors.toList());

            if (release.getSubrouteId() == null) {
                if (!subrouteIds.isEmpty()) {
                    context.disableDefaultConstraintViolation();
                    context.buildConstraintViolationWithTemplate("RELEASE_SUBROUTE_REQUIRED_FOR_GIVEN_ROUTE")
                            .addPropertyNode("subrouteId").addConstraintViolation();
                    valid = false;
                }
            } else if (!subrouteIds.contains(String.valueOf(release.getSubrouteId()))) {
                context.disableDefaultConstraintViolation();
                context.buildConstraintViolationWithTemplate("RELEASE_SUBROUTE_INVALID_FOR_GIVEN_ROUTE")
                        .addPropertyNode("subrouteId").addConstraintViolation();
                valid = false;
            }
        }
        return valid;
    }
}
