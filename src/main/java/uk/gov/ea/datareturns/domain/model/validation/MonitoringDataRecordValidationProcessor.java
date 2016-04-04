/**
 * 
 */
package uk.gov.ea.datareturns.domain.model.validation;

import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;
import javax.validation.ValidatorFactory;

import org.apache.commons.lang3.StringUtils;

import uk.gov.ea.datareturns.domain.io.csv.CSVModel;
import uk.gov.ea.datareturns.domain.model.MonitoringDataRecord;
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

	
	public static final ValidationErrors validateModel(CSVModel<MonitoringDataRecord> model) {
		final ValidationErrors validationErrors = new ValidationErrors();
		
		for (int i = 0; i < model.getRecords().size(); i++) {
			final MonitoringDataRecord record = model.getRecords().get(i);
			final Set<ConstraintViolation<MonitoringDataRecord>> violations = VALIDATOR.validate(record);
			
			for (ConstraintViolation<MonitoringDataRecord> violation : violations) {
				final ValidationError error = new ValidationError();
				error.setFieldName(model.getPojoFieldToHeaderMap().get(violation.getPropertyPath().toString()));
				
				String errorValue = violation.getInvalidValue().toString();
				if (StringUtils.isEmpty(errorValue)) errorValue = null;
				
				error.setErrorValue(errorValue);
				error.setLineNumber(i + 2);
				error.setErrorMessage(violation.getMessage());
				Matcher errorKeyMatcher = ERROR_KEY_PATTERN.matcher(violation.getMessageTemplate());
				
				int errorCode = 0;
				String errorType = "UNKNOWN";
				
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
}
