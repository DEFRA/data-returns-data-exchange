package uk.gov.ea.datareturns.domain.model.validation;

import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;
import uk.gov.ea.datareturns.domain.io.csv.generic.CSVModel;
import uk.gov.ea.datareturns.domain.model.DataSample;
import uk.gov.ea.datareturns.domain.model.rules.FieldDefinition;
import uk.gov.ea.datareturns.domain.result.ValidationError;
import uk.gov.ea.datareturns.domain.result.ValidationErrors;

import javax.inject.Inject;
import javax.validation.ConstraintViolation;
import javax.validation.Path.Node;
import javax.validation.Validator;
import java.util.Objects;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author Sam Gardner-Dell
 *
 */
@Component
public class DataSampleValidator {
    private static final Pattern ERROR_KEY_PATTERN = Pattern.compile("^\\{DR(?<errorCode>\\d{4})-(?<errorType>\\w+)\\}$");

    @Inject
    private Validator validator;

    /**
     * Create a new {@link DataSampleValidator}
     */
    public DataSampleValidator() {

    }

    /**
     * Validate the specified model of {@link DataSample}s
     *
     * @param model the model to be validated
     * @return a {@link ValidationErrors} instance detailing any validation errors (if any) which were found with the model.
     * 		   Use {@link ValidationErrors#isValid()} to determine if any errors were found.
     */
    public final ValidationErrors validateModel(final CSVModel<DataSample> model) {
        final ValidationErrors validationErrors = new ValidationErrors();

        for (final DataSample record : model.getRecords()) {
            final Set<ConstraintViolation<DataSample>> violations = this.validator.validate(record);
            for (final ConstraintViolation<DataSample> violation : violations) {
                final ValidationError error = new ValidationError();
                final String fieldName = getFieldNameForViolation(model, violation);
                String errorValue = null;
                FieldDefinition definition = null;
                // Is the validation specific to a particular column, or is this a cross-field validation?
                if (fieldName != null) {
                    // TODO: How to show the value of a specific field when using a class level dependent field annotation....
                    // See test file CUKE7029_Text_Value_and_Value_and_unit_FAIL.csv
                    if (violation.getInvalidValue() instanceof String) {
                        errorValue = Objects.toString(violation.getInvalidValue(), null);
                    }
                    definition = FieldDefinition.valueOf(fieldName);
                }

                error.setFieldName(fieldName);
                if (definition != null) {
                    error.setDefinition(definition.getDescription());
                }
                error.setErrorValue(errorValue);
                error.setLineNumber(record.getLineNumber());
                error.setErrorMessage(violation.getMessage());
                final Matcher errorKeyMatcher = ERROR_KEY_PATTERN.matcher(violation.getMessageTemplate());

                int errorCode = 0;
                String errorType = "Unknown";

                if (errorKeyMatcher.matches()) {
                    errorCode = Integer.parseInt(errorKeyMatcher.group("errorCode"));
                    errorType = errorKeyMatcher.group("errorType");
                }
                error.setErrorCode(errorCode);
                error.setErrorType(errorType);

                validationErrors.addError(error);
            }
        }
        return validationErrors;
    }

    /**
     * For a given violation, determine the name of the CSV field which caused the error
     *
     * @param model the model being validated
     * @param violation the violation which occurred
     * @return the name of the CSV field which caused the problem.
     */
    private static String getFieldNameForViolation(final CSVModel<DataSample> model,
            final ConstraintViolation<DataSample> violation) {
        // First attempt to retrieve the name of the input field from the declaration on the constraint annotation.
        String validatorAnnotationFieldName = Objects.toString(violation.getConstraintDescriptor().getAttributes().get("fieldName"), null);
        if (StringUtils.isNotEmpty(validatorAnnotationFieldName)) {
            return validatorAnnotationFieldName;
        }

        // Otherwise, try and retrieve the path to the appropriate field using the property path on the annotation.
        // This will not work for class-level annotations (hence the functionality above)
        final Node firstNodeInPath = violation.getPropertyPath().iterator().next();
        return model.getPojoFieldToHeaderMap().get(firstNodeInPath.toString());
    }
}
