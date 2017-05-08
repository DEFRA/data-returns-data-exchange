package uk.gov.ea.datareturns.domain.validation;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;
import uk.gov.ea.datareturns.domain.model.DataSample;
import uk.gov.ea.datareturns.domain.result.ValidationErrors;
import uk.gov.ea.datareturns.domain.validation.MVO;

import javax.validation.ConstraintViolation;
import java.io.IOException;
import java.util.List;
import java.util.Set;

/**
 * @author Graham Willis
 */
public interface MeasurementValidator<V extends MVO> {

    /**
     * Validate a measurement object recording using the javax validation standard
     */
    <V> Set<ConstraintViolation<V>> validateMeasurement(V measurement);

    /**
     * This is a custom serializer for the storage of constraint violations in the database as JSON
     */
    class ViolationSerializer extends StdSerializer<ConstraintViolation> {

        public ViolationSerializer() {
            this(null);
        }

        public ViolationSerializer(Class<ConstraintViolation> violationClass) {
            super(violationClass);
        }

        @Override
        public void serialize(ConstraintViolation violation,
                              JsonGenerator jsonGenerator, SerializerProvider serializerProvider) throws IOException {

            jsonGenerator.writeStartObject();
            jsonGenerator.writeStringField("message", violation.getMessage());
            jsonGenerator.writeStringField("messageTemplate", violation.getMessageTemplate());
            jsonGenerator.writeEndObject();
        }
    }
}