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
	public void initialize(final ValidReturnsDate constraintAnnotation) {

	}

	/* (non-Javadoc)
	 * @see javax.validation.ConstraintValidator#isValid(java.lang.Object, javax.validation.ConstraintValidatorContext)
	 */
	@Override
	public boolean isValid(final Object value, final ConstraintValidatorContext context) {
		boolean isValid = false;
		if (value instanceof String) {
			final String dateValue = String.valueOf(value);

			// If the date value is longer than the standard date-only format then try and parse as a date-time first.
			if (dateValue.length() > DateFormat.STANDARD_DATE_FORMAT.length()) {
				isValid = isValidDateTime(dateValue);
			}
			// If date/time validation failed, try validating as a date-only value
			if (!isValid) {
				isValid = isValidDate(dateValue);
			}
		}
		return isValid;
	}

	private static boolean isValidDate(final String value) {
		boolean isValid = false;

		final LocalDate parsedDate = DateFormat.parseDate(value);
		if (parsedDate != null) {
			final LocalDate now = LocalDate.now();

			// TODO: Future release - extend validation to check for dates too far in the past (should be configurable)
			//			LocalDate earliestAllowedValue = LocalDate.now().minusMonths(18);
			//			isValid = parsedDate.isAfter(earliestAllowedValue)
			//					&& (parsedDate.isEqual(now) || parsedDate.isBefore(now));
			isValid = parsedDate.isEqual(now) || parsedDate.isBefore(now);

		}
		return isValid;
	}

	private static boolean isValidDateTime(final String value) {
		boolean isValid = false;

		final LocalDateTime parsedDate = DateFormat.parseDateTime(value);
		if (parsedDate != null) {
			final LocalDateTime now = LocalDateTime.now();

			// TODO: Future release - extend validation to check for dates too far in the past (should be configurable)
			//			LocalDateTime earliestAllowedValue = LocalDateTime.now().minusMonths(18);
			//			isValid = parsedDate.isAfter(earliestAllowedValue)
			//					&& (parsedDate.isEqual(now) || parsedDate.isBefore(now));
			isValid = parsedDate.isEqual(now) || parsedDate.isBefore(now);
		}
		return isValid;
	}
}