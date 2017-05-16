package uk.gov.ea.datareturns.domain.validation;

import uk.gov.ea.datareturns.domain.validation.landfillmeasurement.LandfillMeasurementMvo;
import uk.gov.ea.datareturns.domain.validation.model.MessageCodes;
import uk.gov.ea.datareturns.domain.validation.model.fields.FieldValue;
import uk.gov.ea.datareturns.domain.validation.model.rules.FieldDefinition;
import uk.gov.ea.datareturns.domain.validation.model.rules.FieldMapping;
import uk.gov.ea.datareturns.domain.result.ValidationErrorField;
import uk.gov.ea.datareturns.domain.result.ValidationErrorType;
import uk.gov.ea.datareturns.domain.result.ValidationErrors;

import javax.inject.Inject;
import javax.validation.ConstraintViolation;
import javax.validation.Validator;
import java.lang.annotation.Annotation;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * @author Sam Gardner-Dell, Graham Willis
 */
public class MeasurementValidatorImpl<V extends Mvo> implements MeasurementValidator<V> {
    private final Map<String, FieldMapping> beanMapping;

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
        this.beanMapping = FieldMapping.getFieldNameToBeanMap(mvoClass);
        this.fieldMessageMap = fieldMessageMap;
    }

    public ValidationErrors validateMeasurement(V measurement) {
        final ValidationErrors validationErrors = new ValidationErrors();
        final Set<ConstraintViolation<V>> violations = validate(measurement);
        for (final ConstraintViolation<V> violation : violations) {
            List<FieldValue<V, ?>> fieldValues = fieldMessageMap.getFieldDependencies(measurement, violation.getMessageTemplate());
        }

        return validationErrors;
    }

    /**
     * Prepare the Error data array for addition to the ValidationError object
     * @param record The current record
     * @param violation the {@link ConstraintViolation} detailing the error
     * @return The data error array
     */
    private List<ValidationErrorField> getErrorDataFromFields(V record, ConstraintViolation<V> violation) {
        List<ValidationErrorField> errorData = new ArrayList<>();
        List<FieldDefinition> fieldsForValidation = getFieldsForViolation(violation);
        if (fieldsForValidation != null) {
            for (FieldDefinition field : fieldsForValidation) {
                FieldMapping fieldMapping = beanMapping.get(field.getName());
                ValidationErrorField errorDatum = new ValidationErrorField();
                if (fieldMapping != null) {
                    errorDatum.setName(field.getName());
                    errorDatum.setValue(fieldMapping.getInputValue(record));
                    errorDatum.setResolvedValue(fieldMapping.getOutputValue(record));
                }
                errorData.add(errorDatum);
            }
        }
        return errorData;
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

    /**
     * Determines the set of FieldDefinitions for each given error code declared in MessageCodes
     * @param violation The hibernate violation
     * @return A list of field definitions
     */
    private List<FieldDefinition> getFieldsForViolation(final ConstraintViolation<V> violation) {
        return MessageCodes.getFieldDependencies(violation.getMessageTemplate());
    }

}
