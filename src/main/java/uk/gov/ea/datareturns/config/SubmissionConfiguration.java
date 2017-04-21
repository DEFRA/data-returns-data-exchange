package uk.gov.ea.datareturns.config;

import javax.inject.Inject;
import javax.validation.Validator;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import uk.gov.ea.datareturns.domain.model.DataSample;
import uk.gov.ea.datareturns.domain.model.validation.DataSampleValidator;
import uk.gov.ea.datareturns.domain.model.validation.DataSampleValidatorImpl;
import uk.gov.ea.datareturns.domain.processors.SubmissionProcessor;

/**
 * @author Graham Willis
 */
@Configuration
public class SubmissionConfiguration {

    @Inject
    Validator validator;

    @Bean
    public DataSampleValidator dataSampleValidator(final Validator validator) {
        return new DataSampleValidatorImpl(validator);
    }

    @Bean
    public SubmissionProcessor<DataSample> dataSampleSubmissionProcessor() {
        return new SubmissionProcessor<>(DataSample[].class, dataSampleValidator(validator));
    }

}
