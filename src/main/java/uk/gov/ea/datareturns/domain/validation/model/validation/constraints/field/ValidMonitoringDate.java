package uk.gov.ea.datareturns.domain.validation.model.validation.constraints.field;

import uk.gov.ea.datareturns.domain.validation.model.MessageCodes;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.*;

/**
 * Annotation to enable hibernate validator based validation of monitoring dates
 *
 * @author Sam Gardner-Dell
 */

// Date can be yyyy-mm-dd or dd-mm-yyyy optionally followed by Thh:mm:ss (e.g. 2016-03-11T09:00:00)
@Target({ ElementType.TYPE })
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = MonitoringDateValidator.class)
@Documented
public @interface ValidMonitoringDate {
    /**
     * Default message template for violations
     * @return the message template to use for violations
     */
    String message() default MessageCodes.Incorrect.Mon_Date;

    /**
     * The message template to use for missing dates
     * @return the message template to use for violations
     */
    String missingMessage() default MessageCodes.Missing.Mon_Date;

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
}
