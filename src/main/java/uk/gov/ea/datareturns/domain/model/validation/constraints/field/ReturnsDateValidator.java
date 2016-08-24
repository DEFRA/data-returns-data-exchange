/**
 *
 */
package uk.gov.ea.datareturns.domain.model.validation.constraints.field;

import java.time.Clock;
import java.time.Instant;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

import org.apache.commons.lang3.StringUtils;
import uk.gov.ea.datareturns.domain.model.ReturnsDate;

/**
 * Validates Monitoring Date values
 *
 * @author Sam Gardner-Dell
 */
public class ReturnsDateValidator implements ConstraintValidator<ValidReturnsDate, Object> {
	private ValidReturnsDate constraintAnnotation;

	/* (non-Javadoc)
	 * @see javax.validation.ConstraintValidator#initialize(java.lang.annotation.Annotation)
	 */
	@Override
	public void initialize(final ValidReturnsDate constraintAnnotation) {
		this.constraintAnnotation = constraintAnnotation;
	}

	/* (non-Javadoc)
	 * @see javax.validation.ConstraintValidator#isValid(java.lang.Object, javax.validation.ConstraintValidatorContext)
	 */
	@Override
	public boolean isValid(final Object value, final ConstraintValidatorContext context) {
		boolean isValid = false;
		if (value instanceof ReturnsDate) {
			final ReturnsDate returnsDate = (ReturnsDate) value;
			isValid = returnsDate.isParsed();

			if (isValid) {
				final Instant instant = returnsDate.getInstant();
				final Instant now = Instant.now(Clock.systemUTC());
				isValid = instant.equals(now) || instant.isBefore(now);

				// TODO: Future release - extend validation to check for dates too far in the past (should be configurable)
				//				final Instant earliestAllowed = now.minus(18, ChronoUnit.MONTHS);
				//				isValid = (instant.equals(now) || instant.isBefore(now)) && instant.isAfter(earliestAllowed);
			} else if (StringUtils.isEmpty(returnsDate.getOriginalValue())) {
				context.disableDefaultConstraintViolation();
				context.buildConstraintViolationWithTemplate(constraintAnnotation.missingMessage()).addConstraintViolation();
			}
		}
		return isValid;
	}
}