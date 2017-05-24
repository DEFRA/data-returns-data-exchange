package uk.gov.ea.datareturns.domain.validation.newmodel.validator;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import uk.gov.ea.datareturns.domain.validation.basicmeasurement.AlternativeValidationObject;
import uk.gov.ea.datareturns.domain.validation.datasample.DataSampleValidationObject;
import uk.gov.ea.datareturns.web.resource.v1.model.record.payload.DataSamplePayload;
import uk.gov.ea.datareturns.web.resource.v1.model.record.payload.DemonstrationAlternativePayload;
import uk.gov.ea.datareturns.web.resource.v1.model.record.payload.Payload;

/**
 * @author Graham Willis
 * Initiated from the data transport object (deserialized JSON)
 * and responsible for defining the specific validations applicable for a given payload
 * type
 */
public class ValidationObjectFactory {
    private static final Logger LOGGER = LoggerFactory.getLogger(ValidationObjectFactory.class);
    /**
     * Generate the validator class from the payload
     * @param payload the payload
     * @return The validator
     */
    public AbstractValidationObject create(Payload payload) {
        if (payload instanceof DataSamplePayload) {
            return new DataSampleValidationObject((DataSamplePayload)payload);
        } else if (payload instanceof DemonstrationAlternativePayload) {
            return new AlternativeValidationObject((DemonstrationAlternativePayload)payload);
        } else {
            LOGGER.error("Not implemented: " + payload.getClass());
        }

        return null;
    }
}
