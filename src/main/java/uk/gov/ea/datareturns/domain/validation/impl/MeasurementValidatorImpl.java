package uk.gov.ea.datareturns.domain.validation.impl;

import uk.gov.ea.datareturns.domain.model.MessageCodes;
import uk.gov.ea.datareturns.domain.model.rules.FieldDefinition;
import uk.gov.ea.datareturns.domain.validation.Mvo;
import uk.gov.ea.datareturns.domain.validation.MeasurementValidator;

import javax.inject.Inject;
import javax.validation.ConstraintViolation;
import javax.validation.Validator;
import java.lang.annotation.Annotation;
import java.util.List;
import java.util.Set;

/**
 * @author Sam Gardner-Dell, Graham Willis
 */
public class MeasurementValidatorImpl<V extends Mvo> implements MeasurementValidator<V> {

    /** validator instance */
    private final Validator instanceValidator;
    /** flag to indicate if the instanceValidator has been fully initialised */
    private boolean initialised = false;

    /**
     * Instantiates a new {@link Mvo} instanceValidator.
     *
     * @param validator the hibernate instanceValidator instance
     */
    @Inject
    public MeasurementValidatorImpl(final Validator validator) {
        this.instanceValidator = validator;
    }

    /**
     * Wrapper to handle synchronisation of the hibernate instanceValidator initialisation.
     * This is necessary because when multiple threads call this.instanceValidator.validate(..) before it is completely initialised, then the
     * initialisation sequence may get run more than once.  This results in any referenced
     * {@link javax.validation.ConstraintValidator#initialize(Annotation)} method being called more than once.
     *
     * @param measurement the record to be validated
     * @param <V> the type of the record
     * @return a set of constraint violations detailing any validation errors that were found
     */
    public <V> Set<ConstraintViolation<V>> validateMeasurement(V measurement) {
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

    /**
     * Determines the set of FieldDefinitions for each given error code declared in MessageCodes
     * @param violation The hibernate violation
     * @return A list of field definitions
     */
    private List<FieldDefinition> getFieldsForViolation(final ConstraintViolation<V> violation) {
        return MessageCodes.getFieldDependencies(violation.getMessageTemplate());
    }

}
