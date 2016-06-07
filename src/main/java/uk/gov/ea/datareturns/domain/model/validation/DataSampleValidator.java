/**
 *
 */
package uk.gov.ea.datareturns.domain.model.validation;

import java.util.Objects;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.inject.Inject;
import javax.validation.ConstraintViolation;
import javax.validation.Path.Node;
import javax.validation.Validator;

import org.springframework.stereotype.Component;

import uk.gov.ea.datareturns.domain.io.csv.generic.CSVModel;
import uk.gov.ea.datareturns.domain.model.DataSample;
import uk.gov.ea.datareturns.domain.model.rules.FieldDefinition;
import uk.gov.ea.datareturns.domain.result.ValidationError;
import uk.gov.ea.datareturns.domain.result.ValidationErrors;

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
				final String errorValue = Objects.toString(violation.getInvalidValue(), null);
				final FieldDefinition definition = FieldDefinition.valueOf(fieldName);

				error.setFieldName(fieldName);
				error.setDefinition(definition.getDescription());
				error.setHelpReference(definition.getHelpReference());
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
		// At the moment the mappings are all done at the top level so we're only interested in the first node on the path, may need to adapt
		// this in future.
		final Node firstNodeInPath = violation.getPropertyPath().iterator().next();
		return model.getPojoFieldToHeaderMap().get(firstNodeInPath.toString());
	}
}
