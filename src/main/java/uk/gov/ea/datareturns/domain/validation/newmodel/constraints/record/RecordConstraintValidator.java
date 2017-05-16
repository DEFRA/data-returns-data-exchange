package uk.gov.ea.datareturns.domain.validation.newmodel.constraints.record;

import javax.validation.ConstraintValidatorContext;

/**
 * Created by sam on 12/10/16.
 */
public interface RecordConstraintValidator<R> {
    boolean isValid(R record, final ConstraintValidatorContext context);
}
