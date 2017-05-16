package uk.gov.ea.datareturns.domain.validation.newmodel.validator;

import uk.gov.ea.datareturns.domain.result.ValidationErrors;
import uk.gov.ea.datareturns.domain.validation.newmodel.entityfields.FieldValue;
import uk.gov.ea.datareturns.domain.validation.newmodel.validator.result.ValidationResult;

import javax.inject.Inject;
import javax.validation.ConstraintViolation;
import javax.validation.Validator;
import java.lang.annotation.Annotation;
import java.util.List;
import java.util.Set;

/**
 * @author Graham Willis
 */
public class MeasurementValidatorImpl<V extends Mvo> implements MeasurementValidator<V> {

    /** validator instance */
    private final Validator instanceValidator;
    /** flag to indicate if the instanceValidator has been fully initialised */
    private boolean initialised = false;

    private FieldMessageMap<V> fieldMessageMap;

    /**
     * Instantiates a new {@link Mvo} instanceValidator.
     *
     * @param validator the hibernate instanceValidator instance
     */
    @Inject
    public MeasurementValidatorImpl(final Validator validator, final Class<V> mvoClass, FieldMessageMap<V> fieldMessageMap) {
        this.instanceValidator = validator;
        this.fieldMessageMap = fieldMessageMap;
    }

    public ValidationResult validateMeasurement(V measurement) {
        final ValidationResult validationResult = new ValidationResult();
        final Set<ConstraintViolation<V>> violations = validate(measurement);
        for (final ConstraintViolation<V> violation : violations) {
            ValidationResult.ValidationError error = validationResult.forViolation(violation);
            List<FieldValue<?>> fieldValues = fieldMessageMap.getFieldDependencies(measurement, violation.getMessageTemplate());
            for (FieldValue<?> fieldValue : fieldValues) {
                error.add(fieldValue);
            }
        }

        return validationResult;
    }

    /**
     * Wrapper to handle synchronisation of the hibernate instanceValidator initialisation.
     * This is necessary because when multiple threads call this.instanceValidator.validate(..) before it is completely initialised, then the
     * initialisation sequence may get run more than once.  This results in any referenced
     * {@link javax.validation.ConstraintValidator#initialize(Annotation)} method being called more than once.
     *
     * @param measurement the measurement to be validated
     * @param <V> the type of the measurement
     * @return a set of constraint violations detailing any validation errors that were found
     */
    private Set<ConstraintViolation<V>> validate(V measurement) {
        Set<ConstraintViolation<V>> violations;
        if (initialised) {
            // Avoid synchronisation if initialised
            violations = this.instanceValidator.validate(measurement);
        } else {
            // Synchronise first call to instanceValidator if not yet initialised.
            synchronized (this) {
                violations = this.instanceValidator.validate(measurement);
            }
            initialised = true;
        }
        return violations;
    }
}
