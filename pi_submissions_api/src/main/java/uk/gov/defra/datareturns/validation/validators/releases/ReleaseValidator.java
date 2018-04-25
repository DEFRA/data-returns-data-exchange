package uk.gov.defra.datareturns.validation.validators.releases;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.hateoas.Link;
import uk.gov.defra.datareturns.data.Context;
import uk.gov.defra.datareturns.data.model.releases.Release;
import uk.gov.defra.datareturns.service.ValueStandardisationService;
import uk.gov.defra.datareturns.validation.service.MasterDataLinks;
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

import static uk.gov.defra.datareturns.validation.util.ValidationUtil.handleError;

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
        valid = checkPositiveReleaseValues(release, context) && valid;
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
            valid = handleError(context, "RELEASE_SUBSTANCE_NOT_SPECIFIED", b -> b.addPropertyNode("substanceId"));
        }
        if (release.getRouteId() == null) {
            valid = handleError(context, "RELEASE_ROUTE_NOT_SPECIFIED", b -> b.addPropertyNode("routeId"));
        }
        if (release.getMethod() == null) {
            valid = handleError(context, "RELEASE_METHOD_NOT_SPECIFIED", b -> b.addPropertyNode("method"));
        }
        return valid;
    }

    private boolean checkValidReleaseSubstance(final Release release, final ConstraintValidatorContext context) {
        boolean valid = true;
        if (release.getSubstanceId() != null) {
            final MdRegime regime = getRegime(release);
            final Set<String> parametersForRoute = cacheService.getParametersByRoute(regime).get(String.valueOf(release.getRouteId()));
            if (parametersForRoute == null || !parametersForRoute.contains(String.valueOf(release.getSubstanceId()))) {
                valid = handleError(context, "RELEASE_SUBSTANCE_INVALID", b -> b.addPropertyNode("substanceId"));
            }
        }
        return valid;
    }

    private boolean checkPositiveReleaseValues(final Release release, final ConstraintValidatorContext context) {
        boolean valid = true;
        if (release.getValue() != null && BigDecimal.ZERO.compareTo(release.getValue()) >= 0) {
            valid = handleError(context, "RELEASE_VALUE_NOT_GREATER_THAN_ZERO", b -> b.addPropertyNode("value"));
        }
        if (release.getNotifiableValue() != null && BigDecimal.ZERO.compareTo(release.getNotifiableValue()) >= 0) {
            valid = handleError(context, "RELEASE_NOTIFIABLE_VALUE_NOT_GREATER_THAN_ZERO", b -> b.addPropertyNode("value"));
        }
        return valid;
    }

    private boolean checkValidReleaseUnit(final Release release, final ConstraintValidatorContext context) {
        boolean valid = true;
        // Must have a valid unit if value is not below the reporting threshold
        if (!Boolean.TRUE.equals(release.getBelowReportingThreshold())) {
            if (release.getUnitId() == null) {
                valid = handleError(context, "RELEASE_UNIT_NOT_SPECIFIED", b -> b.addPropertyNode("unitId"));
            } else {
                final MdRegime regime = getRegime(release);
                final Set<String> unitsForRoute = cacheService.getUnitsByRoute(regime).get(String.valueOf(release.getRouteId()));
                if (unitsForRoute == null || !unitsForRoute.contains(String.valueOf(release.getUnitId()))) {
                    valid = handleError(context, "RELEASE_UNIT_INVALID", b -> b.addPropertyNode("unitId"));
                }
            }
        }
        return valid;
    }


    private boolean checkValidNotifiableRelease(final Release release, final ConstraintValidatorContext context) {
        boolean valid = true;

        if (release.getNotifiableValue() != null) {
            // Must have a valid notifiable release unit value
            if (release.getNotifiableUnitId() == null) {
                valid = handleError(context, "RELEASE_NOTIFIABLE_UNIT_NOT_SPECIFIED", b -> b.addPropertyNode("notifiableUnitId"));
            } else {
                final MdRegime regime = getRegime(release);
                final Set<String> unitsForRoute = cacheService.getUnitsByRoute(regime).get(String.valueOf(release.getRouteId()));
                if (unitsForRoute == null || !unitsForRoute.contains(String.valueOf(release.getNotifiableUnitId()))) {
                    valid = handleError(context, "RELEASE_NOTIFIABLE_UNIT_INVALID", b -> b.addPropertyNode("notifiableUnitId"));
                }
            }

            // Must have a reason for a notifiable release
            if (StringUtils.isEmpty(release.getNotifiableReason())) {
                valid = handleError(context, "RELEASE_NOTIFIABLE_REASON_NOT_SPECIFIED", b -> b.addPropertyNode("notifiableReason"));
            }
        }
        return valid;
    }

    private boolean checkNotifiableReleaseLessThanTotalRelease(final Release release, final ConstraintValidatorContext context) {
        boolean valid = true;

        if (release.getNotifiableValue() != null && release.getNotifiableUnitId() != null) {
            final BigDecimal totalValue = valueStandardisationService.getStandardValue(release.getValue(), String.valueOf(release.getUnitId()));
            final BigDecimal notifiableValue = valueStandardisationService.getStandardValue(release.getNotifiableValue(),
                    String.valueOf(release.getNotifiableUnitId()));

            // TODO - investigate whether a notifiable release in excess of the threshold for a value submitted as brt should be treated as an error
            if (totalValue != null && notifiableValue != null && notifiableValue.compareTo(totalValue) > 0) {
                valid = handleError(context, "RELEASE_NOTIFIABLE_VALUE_EXCEEDS_TOTAL", b -> b.addPropertyNode("notifiableValue"));
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
                    valid = handleError(context, "RELEASE_SUBROUTE_REQUIRED_FOR_GIVEN_ROUTE", b -> b.addPropertyNode("subrouteId"));
                }
            } else if (!subrouteIds.contains(String.valueOf(release.getSubrouteId()))) {
                valid = handleError(context, "RELEASE_SUBROUTE_INVALID_FOR_GIVEN_ROUTE", b -> b.addPropertyNode("subrouteId"));
            }
        }
        return valid;
    }

    private MdRegime getRegime(final Release release) {
        final Object reportingReference = release.getSubmission().getReportingReference();
        final Link regimesLookup = MasterDataLinks.findRegimesForContextAndUniqueIdentifier(Context.PI, Objects.toString(reportingReference));
        final List<MdRegime> regimes = lookupService.list(MdRegime.class, regimesLookup);

        if (regimes.size() != 1) {
            throw new ValidationException("Pollution inventory submissions should be mapped to a single pollution inventory regime.");
        }
        return regimes.get(0);
    }
}
