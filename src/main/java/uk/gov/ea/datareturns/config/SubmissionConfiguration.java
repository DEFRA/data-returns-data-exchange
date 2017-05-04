package uk.gov.ea.datareturns.config;

import javax.inject.Inject;
import javax.validation.Validator;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import uk.gov.ea.datareturns.domain.jpa.dao.userdata.impl.*;
import uk.gov.ea.datareturns.domain.jpa.service.SubmissionService;
import uk.gov.ea.datareturns.domain.model.DataSample;
import uk.gov.ea.datareturns.domain.model.validation.DataSampleValidator;
import uk.gov.ea.datareturns.domain.model.validation.DataSampleValidatorImpl;

/**
 * @author Graham Willis
 */
@Configuration
public class SubmissionConfiguration {

    @Inject private Validator validator;
    @Inject private DatasetDao datasetDao;
    @Inject private UserDao userDao;
    @Inject private RecordDao recordDao;
    @Inject private SubmissionDao submissionDao;

    @Bean
    public DataSampleValidator dataSampleValidator(final Validator validator) {
        return new DataSampleValidatorImpl(validator);
    }

    @Bean
    public SubmissionService<DataSample> dataSampleSubmissionService() {
        return new SubmissionService<>(
                DataSample[].class, dataSampleValidator(validator), userDao,
                datasetDao, recordDao, submissionDao);
    }

}
