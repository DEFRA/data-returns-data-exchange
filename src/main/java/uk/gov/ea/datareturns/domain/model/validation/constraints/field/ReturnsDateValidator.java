/**
 * 
 */
package uk.gov.ea.datareturns.domain.model.validation.constraints.field;

import java.time.LocalDate;
import java.time.LocalDateTime;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

import uk.gov.ea.datareturns.domain.model.rules.DateFormat;

/**
 * Validates Monitoring Date values
 * 
 * @author Sam Gardner-Dell
 */
public class ReturnsDateValidator implements ConstraintValidator<ValidReturnsDate, Object> {
	/* (non-Javadoc)
	 * @see javax.validation.ConstraintValidator#initialize(java.lang.annotation.Annotation)
	 */
	@Override
	public void initialize(ValidReturnsDate constraintAnnotation) {

	}

	/* (non-Javadoc)
	 * @see javax.validation.ConstraintValidator#isValid(java.lang.Object, javax.validation.ConstraintValidatorContext)
	 */
	@Override
	public boolean isValid(Object value, ConstraintValidatorContext context) {
		boolean isValid = false;
		if (value instanceof String) {
			String dateValue = String.valueOf(value);
			isValid = dateValue.contains("T") ? isValidDateTime(dateValue) : isValidDate(dateValue);   
		}
		return isValid;
	}
	
	private static boolean isValidDate(String value) {
		boolean isValid = false;
		
		LocalDate parsedDate = DateFormat.parseDate(value);
		if (parsedDate != null) {
			LocalDate now = LocalDate.now();
			
			// TODO: Future release - extend validation to check for dates too far in the past (should be configurable)
//			LocalDate earliestAllowedValue = LocalDate.now().minusMonths(18);
//			isValid = parsedDate.isAfter(earliestAllowedValue)
//					&& (parsedDate.isEqual(now) || parsedDate.isBefore(now));
			isValid = parsedDate.isEqual(now) || parsedDate.isBefore(now);
			
		}
		return isValid;
	}
	
	private static boolean isValidDateTime(String value) {
		boolean isValid = false;
		
		LocalDateTime parsedDate = DateFormat.parseDateTime(value);
		if (parsedDate != null) {
			LocalDateTime now = LocalDateTime.now();
			
			// TODO: Future release - extend validation to check for dates too far in the past (should be configurable)
//			LocalDateTime earliestAllowedValue = LocalDateTime.now().minusMonths(18);
//			isValid = parsedDate.isAfter(earliestAllowedValue)
//					&& (parsedDate.isEqual(now) || parsedDate.isBefore(now));
			isValid = parsedDate.isEqual(now) || parsedDate.isBefore(now);
		}
		return isValid;
	}
}