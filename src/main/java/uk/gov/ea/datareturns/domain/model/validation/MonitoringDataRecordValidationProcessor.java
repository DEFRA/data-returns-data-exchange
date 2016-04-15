/**
 *
 */
package uk.gov.ea.datareturns.domain.model.validation;

import java.util.Set;
import java.util.concurrent.atomic.AtomicLong;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;
import javax.validation.ValidatorFactory;

import org.apache.commons.lang3.StringUtils;

import uk.gov.ea.datareturns.domain.io.csv.generic.CSVModel;
import uk.gov.ea.datareturns.domain.model.MonitoringDataRecord;
import uk.gov.ea.datareturns.domain.model.rules.FieldDefinition;
import uk.gov.ea.datareturns.domain.result.ValidationError;
import uk.gov.ea.datareturns.domain.result.ValidationErrors;

/**
 * @author sam
 *
 */
public abstract class MonitoringDataRecordValidationProcessor {
	private static final Pattern ERROR_KEY_PATTERN = Pattern.compile("^\\{DR(?<errorCode>\\d{4})-(?<errorType>\\w+)\\}$");

	private static final ValidatorFactory VALIDATOR_FACTORY = Validation.buildDefaultValidatorFactory();
	private static final Validator VALIDATOR = VALIDATOR_FACTORY.getValidator();

	public static final ValidationErrors validateModel(final CSVModel<MonitoringDataRecord> model) {
		final ValidationErrors validationErrors = new ValidationErrors();

		model.getRecords().parallelStream().forEach((record) -> {
			final Set<ConstraintViolation<MonitoringDataRecord>> violations = VALIDATOR.validate(record);
			for (final ConstraintViolation<MonitoringDataRecord> violation : violations) {
				
				final ValidationError error = new ValidationError();
				final String returnsFieldName = model.getPojoFieldToHeaderMap().get(violation.getPropertyPath().toString());
				final String errorValue = StringUtils.defaultIfEmpty(violation.getInvalidValue().toString(), null);
				final FieldDefinition definition = FieldDefinition.valueOf(returnsFieldName);

				error.setFieldName(returnsFieldName);
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
		});
		return validationErrors;
	}
}
