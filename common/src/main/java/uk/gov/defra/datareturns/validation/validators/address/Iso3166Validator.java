package uk.gov.defra.datareturns.validation.validators.address;

import lombok.RequiredArgsConstructor;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Locale;
import java.util.Set;

/**
 * Validate that the annotated value is a valid ISO 3166-2 alpha2 code.
 *
 * @author Sam Gardner-Dell
 */
@RequiredArgsConstructor
public class Iso3166Validator implements ConstraintValidator<Iso3166CountryCode, String> {
    /**
     * Set of ISO 3166-2 alpha2 codes
     */
    private static final Set<String> COUNTRIES = new HashSet<>(Arrays.asList(Locale.getISOCountries()));

    @Override
    public void initialize(final Iso3166CountryCode constraintAnnotation) {
    }

    @Override
    public boolean isValid(final String value, final ConstraintValidatorContext context) {
        return COUNTRIES.contains(value);
    }
}
