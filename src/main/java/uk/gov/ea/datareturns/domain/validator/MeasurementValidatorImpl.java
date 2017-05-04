package uk.gov.ea.datareturns.domain.validator;

import uk.gov.ea.datareturns.domain.dto.MeasurementDto;
import uk.gov.ea.datareturns.domain.result.ValidationErrors;

import javax.validation.Validator;
import java.util.List;

/**
 * @author Graham Willis
 *
 */
public class MeasurementValidatorImpl<T extends MeasurementDto> implements MeasurementValidator<T> {
    /** hibernate validatorInstance instance */
    private final Validator validatorInstance;

    /**
     * Instantiates a new {@link MeasurementDto} validator.
     *
     * @param validatorInstance the hibernate validator instance
     */
    public MeasurementValidatorImpl(final Validator validatorInstance) {
        this.validatorInstance = validatorInstance;
    }

    @Override
    public ValidationErrors validate(List<T> submissions) {
        return null;
    }
}
