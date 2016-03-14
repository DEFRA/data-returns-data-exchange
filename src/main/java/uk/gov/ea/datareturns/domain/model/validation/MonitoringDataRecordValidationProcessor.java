/**
 * 
 */
package uk.gov.ea.datareturns.domain.model.validation;

import java.util.Set;

import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;
import javax.validation.ValidatorFactory;

import org.apache.commons.lang3.StringUtils;

import uk.gov.ea.datareturns.domain.ErrorDetail;
import uk.gov.ea.datareturns.domain.LineError;
import uk.gov.ea.datareturns.domain.SchemaErrors;
import uk.gov.ea.datareturns.domain.io.csv.CSVModel;
import uk.gov.ea.datareturns.domain.model.MonitoringDataRecord;
import uk.gov.ea.datareturns.domain.result.ValidationResult;

/**
 * @author sam
 *
 */
public abstract class MonitoringDataRecordValidationProcessor {
	private static final ValidatorFactory VALIDATOR_FACTORY = Validation.buildDefaultValidatorFactory();
	private static final Validator VALIDATOR = VALIDATOR_FACTORY.getValidator();	

	
	public static final ValidationResult validateModel(CSVModel<MonitoringDataRecord> model) {
		final ValidationResult validationResult = new ValidationResult();
		
		final SchemaErrors schemaErrors = new SchemaErrors();
		
		for (int i = 0; i < model.getRecords().size(); i++) {
			final MonitoringDataRecord record = model.getRecords().get(i);
			final Set<ConstraintViolation<MonitoringDataRecord>> violations = VALIDATOR.validate(record);
			
			for (ConstraintViolation<MonitoringDataRecord> violation : violations) {
				final LineError error = new LineError();
				error.setColumnName(model.getPojoFieldToHeaderMap().get(violation.getPropertyPath().toString()));
				
				String errorValue = violation.getInvalidValue().toString();
				if (StringUtils.isEmpty(errorValue)) errorValue = null;
				
				error.setErrorValue(errorValue);
				error.setOutputLineNo(Integer.toString(i + 2));
				error.setErrorDetail(new ErrorDetail("error", violation.getMessage()));
				schemaErrors.addLineErrror(error);
			}
		}
		
		validationResult.setSchemaErrors(schemaErrors);
		return validationResult;
		
		
	}
}
