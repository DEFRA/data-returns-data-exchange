package uk.gov.ea.datareturns.domain.validator;

import uk.gov.ea.datareturns.domain.dto.MeasurementDto;
import uk.gov.ea.datareturns.domain.result.ValidationErrors;

import java.util.List;

/**
 * @author Graham Willis
 *
 */
public interface MeasurementValidator<T extends MeasurementDto> {

    /**
     * Validate the specified set of {@link MeasurementDto}s
     *
     * @param submissions the set of submitted data to be validated
     * @return a {@link ValidationErrors} instance detailing any validation errors (if any) which were found with the model.
     *          Use {@link ValidationErrors#isValid()} to determine if any errors were found.
     */
    ValidationErrors validate(final List<T> submissions);

}
