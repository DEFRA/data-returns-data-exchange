package uk.gov.ea.datareturns.domain.processors;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import uk.gov.ea.datareturns.domain.model.Payload;

import java.io.IOException;

/**
 * @author Graham Willis
 */
public class SubmissionProcessor<T extends Payload> {
    private static final Logger LOGGER = LoggerFactory.getLogger(SubmissionProcessor.class);
    private final Class<T> dataSampleClass;
    private final static ObjectMapper mapper = new ObjectMapper();

    public SubmissionProcessor(Class<T> dataSampleClass) {
        LOGGER.info("Initializing submission processor for payload type: " + dataSampleClass.getSimpleName());
        mapper.disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES);
        this.dataSampleClass = dataSampleClass;
    }

    /**
     * Parse JSON and return the payload OR null if the JSON cannot be parsed
     */
    public T parse(String json) {
        try {
            return mapper.readValue(json, dataSampleClass);
        } catch (IOException e) {
            LOGGER.info("Cannot parse JSON: " + json);
            return null;
        }
    }
}
