package uk.gov.ea.datareturns.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import uk.gov.ea.datareturns.domain.model.DataSample;
import uk.gov.ea.datareturns.domain.processors.SubmissionProcessor;

/**
 * @author Graham Willis
 */
@Configuration
public class SubmissionConfiguration {

    @Bean
    public SubmissionProcessor<DataSample> dataSampleSubmissionProcessor() {
        return new SubmissionProcessor<>(DataSample.class);
    }

}
