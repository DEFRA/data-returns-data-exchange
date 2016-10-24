package uk.gov.ea.datareturns.domain.model.validation;

import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;
import uk.gov.ea.datareturns.domain.model.DataSample;
import uk.gov.ea.datareturns.domain.model.fields.FieldValue;
import uk.gov.ea.datareturns.domain.model.rules.FieldDefinition;
import uk.gov.ea.datareturns.domain.model.rules.FieldMapping;
import uk.gov.ea.datareturns.domain.result.DependencyValidationError;
import uk.gov.ea.datareturns.domain.result.ValidationError;
import uk.gov.ea.datareturns.domain.result.ValidationErrors;

import javax.inject.Inject;
import javax.validation.ConstraintViolation;
import javax.validation.Path.Node;
import javax.validation.Validator;
import java.util.Collection;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * The type Data sample validator.
 * @author Sam Gardner-Dell
 */
@Component
public class DataSampleValidator {
    private static final Pattern ERROR_KEY_PATTERN = Pattern.compile("^\\{DR(?<errorCode>\\d{4})-(?<errorType>\\w+)\\}$");

    private static final Map<String, String> FIELD_MAPPING = FieldMapping.getBeanToFieldNameMap(DataSample.class);

    /** hibernate validator instance */
    private final Validator validator;
    private final String COMBINATION = "Combination"; //TODO - String constants class for error type?

    /**
     * Instantiates a new {@link DataSample} validator.
     *
     * @param validator the hibernate validator instance
     */
    @Inject
    public DataSampleValidator(final Validator validator) {
        this.validator = validator;
    }

    /**
     * Validate the specified model of {@link DataSample}s
     *
     * @param model the model to be validated
     * @return a {@link ValidationErrors} instance detailing any validation errors (if any) which were found with the model. 		   Use {@link ValidationErrors#isValid()} to determine if any errors were found.
     */
    public final ValidationErrors validateModel(final Collection<DataSample> model) {
        final ValidationErrors validationErrors = new ValidationErrors();

        // Record number starts at 2 (line 1 = header, line 2 = start of data)
        int recordNumber = 2;
        for (final DataSample record : model) {
            final Set<ConstraintViolation<DataSample>> violations = this.validator.validate(record);
            for (final ConstraintViolation<DataSample> violation : violations) {
                int errorCode = 0;
                String errorType = "Unknown";

                final Matcher errorKeyMatcher = ERROR_KEY_PATTERN.matcher(violation.getMessageTemplate());
                if (errorKeyMatcher.matches()) {
                    errorCode = Integer.parseInt(errorKeyMatcher.group("errorCode"));
                    errorType = errorKeyMatcher.group("errorType");
                }

                ValidationError error = null;
                if (errorType.equals(COMBINATION)) {
                    error = new DependencyValidationError(
                            (record.getReturnType() != null) ? record.getReturnType().getEntity().getName() : null,
                            null,
                            (record.getParameter() != null) ? record.getParameter().getEntity().getName() : null,
                            (record.getUnit() != null) ? record.getUnit().getEntity().getName() : null
                    );
                } else {
                    error = new ValidationError();
                }

                String errorValue = null;
                FieldDefinition definition = getFieldForViolation(model, violation);
                if (violation.getInvalidValue() instanceof String) {
                    errorValue = (String) violation.getInvalidValue();
                } else if (violation.getInvalidValue() instanceof FieldValue) {
                    errorValue = ((FieldValue) violation.getInvalidValue()).getInputValue();
                }

                if (definition != null) {
                    error.setFieldName(definition.getName());
                    error.setDefinition(definition.getDescription());
                }
                error.setErrorValue(errorValue);
                error.setLineNumber(recordNumber);
                error.setErrorMessage(violation.getMessage());

                error.setErrorCode(errorCode);
                error.setErrorType(errorType);

                validationErrors.addError(error);
            }
            recordNumber++;
        }
        return validationErrors;
    }

    /**
     * For a given violation, determine the name of the CSV field which caused the error
     *
     * @param model the model being validated
     * @param violation the violation which occurred
     * @return the {@link FieldDefinition} for the CSV field which caused the problem.
     */
    private static FieldDefinition getFieldForViolation(final Collection<DataSample> model,
            final ConstraintViolation<DataSample> violation) {
        // First attempt to retrieve the name of the input field from the declaration on the constraint annotation.
        String validatorAnnotationFieldName = Objects.toString(violation.getConstraintDescriptor().getAttributes().get("fieldName"), null);
        if (StringUtils.isNotEmpty(validatorAnnotationFieldName)) {
            return FieldDefinition.valueOf(validatorAnnotationFieldName);
        }

        if (violation.getInvalidValue() instanceof FieldValue) {
            FieldValue fv = (FieldValue) violation.getInvalidValue();
            return fv.getField();
        }

        // Otherwise, try and retrieve the path to the appropriate field using the property path on the annotation.
        // This will not work for class-level annotations (hence the functionality above)
        final Node firstNodeInPath = violation.getPropertyPath().iterator().next();
        String mapping = FIELD_MAPPING.get(firstNodeInPath.toString());
        if (mapping != null) {
            return FieldDefinition.valueOf(mapping);
        }

        // Try to retrieve the fieldname directly
        FieldDefinition fieldDefinition = FieldDefinition.forFieldName(firstNodeInPath.toString());
        if (fieldDefinition != null) {
            return fieldDefinition;
        }

        return null;
    }
}
