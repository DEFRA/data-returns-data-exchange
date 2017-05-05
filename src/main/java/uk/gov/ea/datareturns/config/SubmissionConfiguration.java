package uk.gov.ea.datareturns.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import uk.gov.ea.datareturns.domain.dto.impl.BasicMeasurementDto;
import uk.gov.ea.datareturns.domain.jpa.dao.masterdata.ParameterDao;
import uk.gov.ea.datareturns.domain.jpa.dao.userdata.impl.DatasetDao;
import uk.gov.ea.datareturns.domain.jpa.dao.userdata.impl.RecordDao;
import uk.gov.ea.datareturns.domain.jpa.dao.userdata.impl.UserDao;
import uk.gov.ea.datareturns.domain.jpa.entities.userdata.AbstractMeasurementFactory;
import uk.gov.ea.datareturns.domain.jpa.entities.userdata.impl.BasicMeasurement;
import uk.gov.ea.datareturns.domain.jpa.entities.userdata.impl.BasicMeasurementFactory;
import uk.gov.ea.datareturns.domain.jpa.service.SubmissionService;
import uk.gov.ea.datareturns.domain.model.validation.ModelValidator;
import uk.gov.ea.datareturns.domain.model.validation.ModelValidatorImpl;
import uk.gov.ea.datareturns.domain.validation.MVOFactory;
import uk.gov.ea.datareturns.domain.validation.impl.BasicMeasurementMVO;

import javax.inject.Inject;
import javax.validation.Validator;

/**
 * @author Graham Willis
 */
@Configuration
public class SubmissionConfiguration {

    @Inject private Validator validator;
    @Inject private DatasetDao datasetDao;
    @Inject private UserDao userDao;
    @Inject private RecordDao recordDao;
    @Inject private ParameterDao parameterDao;

    @Bean
    public MVOFactory<BasicMeasurementDto, BasicMeasurementMVO> mvoFactory() {
        return new MVOFactory<>(BasicMeasurementMVO.class);
    }

    @Bean
    public ModelValidator<BasicMeasurementMVO> basicMeasurementValidator() {
        return new ModelValidatorImpl<>(this.validator, BasicMeasurementMVO.class);
    }

    @Bean
    public AbstractMeasurementFactory<BasicMeasurement, BasicMeasurementDto> measurementFactory() {
        return new BasicMeasurementFactory(parameterDao);
    }

    @Bean
    public SubmissionService<BasicMeasurementDto, BasicMeasurement, BasicMeasurementMVO> basicSubmissionService() {
        return new SubmissionService<>(BasicMeasurementDto.class,
                BasicMeasurementDto[].class,
                BasicMeasurement.class, mvoFactory(),
                userDao, datasetDao, recordDao,
                basicMeasurementValidator(),
                measurementFactory());
    }
}
