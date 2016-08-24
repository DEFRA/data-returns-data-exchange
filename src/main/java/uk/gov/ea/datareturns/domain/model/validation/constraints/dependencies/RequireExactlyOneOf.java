package uk.gov.ea.datareturns.domain.model.validation.constraints.dependencies;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.*;

/**
 * Annotation to enable hibernate validator based validation of interdependent values within a class
 *
 * @author Sam Gardner-Dell
 */
@Target({ ElementType.TYPE })
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = RequireExactlyOneOfValidator.class)
@Repeatable(RequireExactlyOneOf.List.class)
@Documented
public @interface RequireExactlyOneOf {
	/**
	 * @return the constraint violation template
	 */
	String message() default "{uk.gov.ea.datareturns.domain.model.validation.onerequired.default.message}";
	/**
	 * @return the constraint violation template
	 */
	String tooFewMessage() default "{uk.gov.ea.datareturns.domain.model.validation.onerequired.toofew.message}";
	/**
	 * @return the constraint violation template
	 */
	String tooManyMessage() default "{uk.gov.ea.datareturns.domain.model.validation.onerequired.toomany.message}";

	/**
	 * Validation groups
	 * @return the groups that this validator is associated with
	 */
	Class<?>[] groups() default {};

	/**
	 * Validation payload
	 * @return the Payload
	 */
	Class<? extends Payload>[] payload() default {};

	/**
	 * Retrieve an array of getter methods that will be invoked to test that exactly one field is non-null
	 *
	 * @return an array of Strings representing getter methods
	 */
	String[] fieldGetters();

	/**
	 * Container annotation for {@link RequireExactlyOneOf}
	 *
	 * @author Sam Gardner-Dell
	 */
	@Target({ ElementType.TYPE })
	@Retention(RetentionPolicy.RUNTIME)
	@Documented
	public @interface List {
		RequireExactlyOneOf[] value();
	}
}