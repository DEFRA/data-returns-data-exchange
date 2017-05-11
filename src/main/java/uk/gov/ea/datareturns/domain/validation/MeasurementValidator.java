package uk.gov.ea.datareturns.domain.validation;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;
import uk.gov.ea.datareturns.domain.result.ValidationErrors;

import javax.validation.ConstraintViolation;
import java.io.IOException;
import java.util.Set;

/**
 * @author Graham Willis
 */
public interface MeasurementValidator<V extends Mvo> {

    /**
     * Validate a measurement object recording using the javax validation standard
     */
    ValidationErrors validateMeasurement(V measurement);
}