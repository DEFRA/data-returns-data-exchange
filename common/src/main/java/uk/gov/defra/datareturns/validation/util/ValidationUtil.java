package uk.gov.defra.datareturns.validation.util;

import javax.validation.ConstraintValidatorContext;
import java.util.function.Consumer;

/**
 * Simple helper methods for JSR303 bean validation
 *
 * @author Sam Gardner-Dell
 */
public final class ValidationUtil {


    private ValidationUtil() {
    }


    /**
     * Construct a new constraint violation
     *
     * @param context the validator context
     * @param message the message associated with the constraint violation
     * @return always returns false (to indicate validation failed)
     */
    public static boolean handleError(final ConstraintValidatorContext context, final String message) {
        return handleError(context, message, null);
    }

    /**
     * Construct a new constraint violation and optionally add property paths etc
     *
     * @param context         the validator context
     * @param message         the message associated with the constraint violation
     * @param builderConsumer an optional {@link Consumer} to customise the generated constraint violation
     * @return always returns false (to indicate validation failed)
     */
    public static boolean handleError(final ConstraintValidatorContext context, final String message,
                                      final Consumer<ConstraintValidatorContext.ConstraintViolationBuilder> builderConsumer) {
        context.disableDefaultConstraintViolation();
        final ConstraintValidatorContext.ConstraintViolationBuilder builder = context.buildConstraintViolationWithTemplate(message);
        if (builderConsumer != null) {
            builderConsumer.accept(builder);
        }
        builder.addConstraintViolation();
        return false;
    }
}
