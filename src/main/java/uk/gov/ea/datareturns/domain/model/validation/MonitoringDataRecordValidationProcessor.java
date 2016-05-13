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
import uk.gov.ea.datareturns.domain.model.MonitoringDataRecord;
import uk.gov.ea.datareturns.domain.model.rules.FieldDefinition;
import uk.gov.ea.datareturns.domain.result.ValidationError;
import uk.gov.ea.datareturns.domain.result.ValidationErrors;

/**
 * @author Sam Gardner-Dell
 *
 */
@Component
public class MonitoringDataRecordValidationProcessor {
	private static final Pattern ERROR_KEY_PATTERN = Pattern.compile("^\\{DR(?<errorCode>\\d{4})-(?<errorType>\\w+)\\}$");

	@Inject
	private Validator validator;

	public MonitoringDataRecordValidationProcessor() {

	}

	public final ValidationErrors validateModel(final CSVModel<MonitoringDataRecord> model) {
		final ValidationErrors validationErrors = new ValidationErrors();

		for (final MonitoringDataRecord record : model.getRecords()) {
			final Set<ConstraintViolation<MonitoringDataRecord>> violations = this.validator.validate(record);
			for (final ConstraintViolation<MonitoringDataRecord> violation : violations) {
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

	private static String getFieldNameForViolation(final CSVModel<MonitoringDataRecord> model,
			final ConstraintViolation<MonitoringDataRecord> violation) {
		// At the moment the mappings are all done at the top level so we're only interested in the first node on the path, may need to adapt
		// this in future.
		final Node firstNodeInPath = violation.getPropertyPath().iterator().next();
		final String mapping = model.getPojoFieldToHeaderMap().get(firstNodeInPath.toString());
		return mapping;
	}
}
