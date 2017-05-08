package uk.gov.ea.datareturns.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import uk.gov.ea.datareturns.domain.dto.impl.BasicMeasurementDto;
import uk.gov.ea.datareturns.domain.jpa.dao.masterdata.ParameterDao;
import uk.gov.ea.datareturns.domain.jpa.dao.userdata.impl.DatasetDao;
import uk.gov.ea.datareturns.domain.jpa.dao.userdata.impl.RecordDao;
import uk.gov.ea.datareturns.domain.jpa.dao.userdata.impl.MeasurementDao;
import uk.gov.ea.datareturns.domain.jpa.dao.userdata.impl.UserDao;
import uk.gov.ea.datareturns.domain.jpa.entities.userdata.AbstractMeasurement;
import uk.gov.ea.datareturns.domain.jpa.entities.userdata.AbstractMeasurementFactory;
import uk.gov.ea.datareturns.domain.jpa.entities.userdata.impl.BasicMeasurement;
import uk.gov.ea.datareturns.domain.jpa.entities.userdata.impl.BasicMeasurementFactory;
import uk.gov.ea.datareturns.domain.jpa.service.SubmissionService;
import uk.gov.ea.datareturns.domain.validation.MeasurementValidator;
import uk.gov.ea.datareturns.domain.validation.impl.MeasurementValidatorImpl;
import uk.gov.ea.datareturns.domain.validation.MVOFactory;
import uk.gov.ea.datareturns.domain.validation.impl.BasicMeasurementMVO;

import javax.inject.Inject;
import javax.validation.Validator;

/**
 * @author Graham Willis
 */
@Configuration
public class SubmissionConfiguration {

    private Validator validator;
    private DatasetDao datasetDao;
    private UserDao userDao;
    private RecordDao recordDao;
    private ParameterDao parameterDao;

    @Inject
    public SubmissionConfiguration(
            Validator validator,
            DatasetDao datasetDao,
            UserDao userDao,
            RecordDao recordDao,
            ParameterDao parameterDao
    ) {
        this.validator = validator;
        this.datasetDao = datasetDao;
        this.userDao = userDao;
        this.recordDao = recordDao;
        this.parameterDao = parameterDao;
    }

    @Bean
    public MeasurementDao<? extends AbstractMeasurement> basicMeasuementDao() {
        return new MeasurementDao(BasicMeasurement.class);
    }

    @Bean
    public MVOFactory<BasicMeasurementDto, BasicMeasurementMVO> mvoFactory() {
        return new MVOFactory<>(BasicMeasurementMVO.class);
    }

    @Bean
    public MeasurementValidator<BasicMeasurementMVO> basicMeasurementValidator() {
        return new MeasurementValidatorImpl<>(this.validator);
    }

    @Bean
    public AbstractMeasurementFactory<BasicMeasurement, BasicMeasurementDto> measurementFactory() {
        return new BasicMeasurementFactory(parameterDao);
    }

    @Bean
    public SubmissionService<BasicMeasurementDto, BasicMeasurement, BasicMeasurementMVO> basicSubmissionService() {
        return new SubmissionService<>(
                BasicMeasurementDto.class,
                BasicMeasurementDto[].class,
                BasicMeasurement.class,
                mvoFactory(),
                userDao,
                datasetDao,
                recordDao,
                basicMeasuementDao(),
                basicMeasurementValidator(),
                measurementFactory());
    }
}
