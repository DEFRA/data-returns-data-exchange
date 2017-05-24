package uk.gov.ea.datareturns.domain.validation.newmodel.validator;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import uk.gov.ea.datareturns.domain.validation.datasample.DataSampleMvo;
import uk.gov.ea.datareturns.web.resource.ObservationSerializationBean;
import uk.gov.ea.datareturns.web.resource.v1.model.record.payload.DataSamplePayload;
import uk.gov.ea.datareturns.web.resource.v1.model.record.payload.DemonstrationAlternativePayload;
import uk.gov.ea.datareturns.web.resource.v1.model.record.payload.Payload;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;

/**
 * @author Graham Willis
 * Initiated from the data transport object (deserialized JSON)
 * and responsible for defining the specific validations applicable for a given payload
 * type
 */
public class NewMvoFactory {
    private static final Logger LOGGER = LoggerFactory.getLogger(NewMvoFactory.class);
    /**
     * Generate the validator class from the payload
     * @param payload the payload
     * @return The validator
     */
    public Mvo create(Payload payload) {

        if (payload instanceof DataSamplePayload) {
            Mvo mvo = new DataSampleMvo((DataSamplePayload)payload);
            return mvo;
        } else if (payload instanceof DemonstrationAlternativePayload) {
            LOGGER.error("Not implemented: " + payload.getClass());

        } else {
            LOGGER.error("Not implemented: " + payload.getClass());
        }

        return null;
    }
}
