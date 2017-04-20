package uk.gov.ea.datareturns.domain.processors;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import uk.gov.ea.datareturns.domain.model.Payload;
import uk.gov.ea.datareturns.domain.model.validation.DataSampleValidator;
import uk.gov.ea.datareturns.domain.result.ValidationErrors;

import java.io.IOException;
import java.util.Arrays;

/**
 * @author Graham Willis
 */
public class SubmissionProcessor<T extends Payload> {
    private static final Logger LOGGER = LoggerFactory.getLogger(SubmissionProcessor.class);
    private final Class<T[]> dataSampleClass;
    private final static ObjectMapper mapper = new ObjectMapper();
    private final DataSampleValidator validator;

    public SubmissionProcessor(Class<T[]> dataSampleClass, DataSampleValidator validator) {
        LOGGER.info("Initializing submission processor for payload type: " + dataSampleClass.getSimpleName());
        mapper.disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES);
        mapper.enable(DeserializationFeature.ACCEPT_SINGLE_VALUE_AS_ARRAY);
        this.dataSampleClass = dataSampleClass;
        this.validator = validator;
    }

    /**
     * Parse JSON and return the an array of samples OR null if the JSON cannot be parsed
     */
    public T[] parse(String json) {
        try {
            return mapper.readValue(json, dataSampleClass);
        } catch (IOException e) {
            LOGGER.info("Cannot parse JSON: " + json);
            return null;
        }
    }

    /**
     * Validate samples
     * @param samples
     */
    public ValidationErrors validate(T[] samples) {
        return validator.validateModel(Arrays.asList(samples));
    }
}
