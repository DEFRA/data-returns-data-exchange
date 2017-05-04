package uk.gov.ea.datareturns.config;

import javax.inject.Inject;
import javax.validation.Validator;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import uk.gov.ea.datareturns.domain.dto.impl.BasicMeasurementDto;
import uk.gov.ea.datareturns.domain.jpa.dao.userdata.impl.*;
import uk.gov.ea.datareturns.domain.jpa.entities.userdata.impl.BasicMeasurement;
import uk.gov.ea.datareturns.domain.jpa.service.SubmissionService;
import uk.gov.ea.datareturns.domain.validator.MeasurementValidator;
import uk.gov.ea.datareturns.domain.validator.MeasurementValidatorImpl;

/**
 * @author Graham Willis
 */
@Configuration
public class SubmissionConfiguration {

    @Inject private Validator validator;
    @Inject private DatasetDao datasetDao;
    @Inject private UserDao userDao;
    @Inject private RecordDao recordDao;

    @Bean
    @Inject
    public MeasurementValidator<BasicMeasurementDto> dataSampleValidator(final Validator validator) {
        return new MeasurementValidatorImpl<>(validator);
    }

    @Bean
    public SubmissionService<BasicMeasurementDto, BasicMeasurement> basicSubmissionService() {
        return new SubmissionService<>(BasicMeasurementDto.class, BasicMeasurementDto[].class,
                BasicMeasurement.class, userDao, datasetDao, recordDao, dataSampleValidator(this.validator));
    }
}
