package uk.gov.ea.datareturns.domain.model.validation;

import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import uk.gov.ea.datareturns.domain.model.DataSample;
import uk.gov.ea.datareturns.domain.model.MessageCodes;
import uk.gov.ea.datareturns.domain.model.rules.FieldDefinition;
import uk.gov.ea.datareturns.domain.model.rules.FieldMapping;
import uk.gov.ea.datareturns.domain.result.ValidationError;
import uk.gov.ea.datareturns.domain.result.ValidationErrors;

import javax.inject.Inject;
import javax.validation.ConstraintViolation;
import javax.validation.Validator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * DataSample validator implementation
 *
 * @author Sam Gardner-Dell
 */
@Component
@Scope(ConfigurableBeanFactory.SCOPE_SINGLETON)
public class DataSampleValidatorImpl implements DataSampleValidator {
    private static final Pattern ERROR_KEY_PATTERN = Pattern.compile("^\\{DR(?<errorCode>\\d{4})-(?<errorType>\\w+)\\}$");
    private static final Map<String, FieldMapping> BEAN_MAPPING = FieldMapping.getFieldNameToBeanMap(DataSample.class);

    /** hibernate validator instance */
    private final Validator validator;

    /**
     * Instantiates a new {@link DataSample} validator.
     *
     * @param validator the hibernate validator instance
     */
    @Inject
    public DataSampleValidatorImpl(final Validator validator) {
        this.validator = validator;
    }

    /**
     * Validate the specified model of {@link DataSample}s
     *
     * @param model the model to be validated
     * @return a {@link ValidationErrors} instance detailing any validation errors (if any) which were found with the model. 		   Use {@link ValidationErrors#isValid()} to determine if any errors were found.
     */
    public final ValidationErrors validateModel(final List<DataSample> model) {
        final ValidationErrors validationErrors = new ValidationErrors();

        int index = 0;
        for (final DataSample record : model) {
            final Set<ConstraintViolation<DataSample>> violations = this.validator.validate(record, ValidationGroups.OrderedChecks.class);
            for (final ConstraintViolation<DataSample> violation : violations) {
                int errorCode = 0;
                String errorType = "Unknown";

                final Matcher errorKeyMatcher = ERROR_KEY_PATTERN.matcher(violation.getMessageTemplate());
                if (errorKeyMatcher.matches()) {
                    errorCode = Integer.parseInt(errorKeyMatcher.group("errorCode"));
                    errorType = errorKeyMatcher.group("errorType");
                }

                List<FieldDefinition> fieldsForValidation = getFieldsForViolation(violation);
                ValidationError.ErrorData[] errorData = getErrorDataFromFields(record, fieldsForValidation);

                ValidationError error = new ValidationError();

                error.setRecordIndex(index);
                error.setErrorData(errorData);
                error.setErrorMessage(violation.getMessage());

                error.setErrorCode(errorCode);
                error.setErrorType(errorType);

                validationErrors.addError(error);
            }
            index++;
        }
        return validationErrors;
    }

    /**
     * Prepare the Error data array for addition to the ValidationError object
     * @param record The current record
     * @param fieldsForValidation The set of field definitions to initialize the data error array from
     * @return The data error array
     */
    private ValidationError.ErrorData[] getErrorDataFromFields(DataSample record, List<FieldDefinition> fieldsForValidation) {
        if (fieldsForValidation == null || fieldsForValidation.size() == 0) {
            return new ValidationError.ErrorData[0];
        } else {
            ValidationError.ErrorData[] errData = new ValidationError.ErrorData[fieldsForValidation.size()];
            int errDataIdx = 0;
            for (FieldDefinition field : fieldsForValidation) {
                FieldMapping fieldMapping = BEAN_MAPPING.get(field.getName());
                ValidationError.ErrorData errorDatum = new ValidationError.ErrorData();
                if (fieldMapping != null) {
                    errorDatum.setFieldName(field.getName());
                    errorDatum.setErrorValue(fieldMapping.getInputValue(record));
                    errorDatum.setResolvedValue(fieldMapping.getOutputValue(record));
                }
                errData[errDataIdx++] = errorDatum;
            }
            return errData;
        }
    }

    /**
     * Determines the set of FieldDefinitions for each given error code declared in MessageCodes
     * @param violation The hibernate violation
     * @return A list of field definitions
     */
    private List<FieldDefinition> getFieldsForViolation(final ConstraintViolation<DataSample> violation) {
        return MessageCodes.getFieldDependencies(violation.getMessageTemplate());
    }
}