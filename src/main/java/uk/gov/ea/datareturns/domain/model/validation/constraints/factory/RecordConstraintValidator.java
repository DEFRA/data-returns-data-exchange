package uk.gov.ea.datareturns.domain.model.validation.constraints.factory;

import javax.validation.ConstraintValidatorContext;

/**
 * Created by sam on 12/10/16.
 */
public interface RecordConstraintValidator<R> {
    boolean isValid(R record, final ConstraintValidatorContext context);
}
