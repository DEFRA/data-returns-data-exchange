package uk.gov.ea.datareturns.domain.model.validation;

import uk.gov.ea.datareturns.domain.model.DataSample;
import uk.gov.ea.datareturns.domain.result.ValidationErrors;
import uk.gov.ea.datareturns.domain.validation.MVO;

import java.util.List;

/**
 * Provides {@link DataSample} validation functionality
 *
 * @author Sam Gardner-Dell
 */
public interface ModelValidator<T extends MVO> {

    /**
     * Validate the specified model of {@link DataSample}s
     *
     * @param model the model to be validated
     * @return a {@link ValidationErrors} instance detailing any validation errors (if any) which were found with the model.
     *          Use {@link ValidationErrors#isValid()} to determine if any errors were found.
     */
    ValidationErrors validateModel(final List<T> model);
}