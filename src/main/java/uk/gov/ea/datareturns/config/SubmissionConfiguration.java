package uk.gov.ea.datareturns.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import uk.gov.ea.datareturns.domain.dto.impl.BasicMeasurementDto;
import uk.gov.ea.datareturns.domain.dto.impl.LandfillMeasurementDto;
import uk.gov.ea.datareturns.domain.jpa.dao.masterdata.*;
import uk.gov.ea.datareturns.domain.jpa.dao.userdata.impl.DatasetDao;
import uk.gov.ea.datareturns.domain.jpa.dao.userdata.impl.RecordDao;
import uk.gov.ea.datareturns.domain.jpa.dao.userdata.impl.MeasurementDao;
import uk.gov.ea.datareturns.domain.jpa.dao.userdata.impl.UserDao;
import uk.gov.ea.datareturns.domain.jpa.dao.userdata.factories.AbstractMeasurementFactory;
import uk.gov.ea.datareturns.domain.jpa.entities.userdata.impl.BasicMeasurement;
import uk.gov.ea.datareturns.domain.jpa.dao.userdata.factories.impl.BasicMeasurementFactory;
import uk.gov.ea.datareturns.domain.jpa.entities.userdata.impl.LandfillMeasurement;
import uk.gov.ea.datareturns.domain.jpa.dao.userdata.factories.impl.LandfillMeasurementFactory;
import uk.gov.ea.datareturns.domain.jpa.service.SubmissionService;
import uk.gov.ea.datareturns.domain.validation.MeasurementValidator;
import uk.gov.ea.datareturns.domain.validation.impl.LandfillMeasurementMvo;
import uk.gov.ea.datareturns.domain.validation.impl.MeasurementValidatorImpl;
import uk.gov.ea.datareturns.domain.validation.MvoFactory;
import uk.gov.ea.datareturns.domain.validation.impl.BasicMeasurementMvo;

import javax.inject.Inject;
import javax.validation.Validator;

/**
 * @author Graham Willis
 */
@Configuration
public class SubmissionConfiguration {

    private final Validator validator;
    private final DatasetDao datasetDao;
    private final UserDao userDao;
    private final RecordDao recordDao;
    private final MethodOrStandardDao methodOrStandardDao;
    private final ParameterDao parameterDao;
    private final QualifierDao qualifierDao;
    private final ReferencePeriodDao referencePeriodDao;
    private final ReturnPeriodDao returnPeriodDao;
    private final ReturnTypeDao returnTypeDao;
    private final SiteDao siteDao;
    private final TextValueDao textValueDao;
    private final UniqueIdentifierAliasDao uniqueIdentifierAliasDao;
    private final UniqueIdentifierDao uniqueIdentifierDao;
    private final UnitDao unitDao;

    @Inject
    public SubmissionConfiguration(
            Validator validator,
            DatasetDao datasetDao,
            UserDao userDao,
            RecordDao recordDao,
            MethodOrStandardDao methodOrStandardDao,
            ParameterDao parameterDao,
            QualifierDao qualifierDao,
            ReferencePeriodDao referencePeriodDao,
            ReturnPeriodDao returnPeriodDao,
            ReturnTypeDao returnTypeDao,
            SiteDao siteDao,
            TextValueDao textValueDao,
            UniqueIdentifierAliasDao uniqueIdentifierAliasDao,
            UniqueIdentifierDao uniqueIdentifierDao,
            UnitDao unitDao
    ) {
        this.validator = validator;
        this.datasetDao = datasetDao;
        this.userDao = userDao;
        this.recordDao = recordDao;
        this.methodOrStandardDao = methodOrStandardDao;
        this.returnPeriodDao = returnPeriodDao;
        this.parameterDao = parameterDao;
        this.qualifierDao = qualifierDao;
        this.referencePeriodDao = referencePeriodDao;
        this.returnTypeDao = returnTypeDao;
        this.siteDao = siteDao;
        this.textValueDao = textValueDao;
        this.uniqueIdentifierAliasDao = uniqueIdentifierAliasDao;
        this.uniqueIdentifierDao = uniqueIdentifierDao;
        this.unitDao = unitDao;
    }

    /**
     * The data access object for the BasicMeasurement Entity
     * @return
     */
    @Bean
    public MeasurementDao<BasicMeasurement> basicMeasuementDao() {
        return new MeasurementDao<>(BasicMeasurement.class);
    }

    /**
     * The factory to create Basic measurement objects Mvo for validation
     * @return
     */
    @Bean
    public AbstractMeasurementFactory<BasicMeasurement, BasicMeasurementDto> basicMeasurementFactory() {
        return new BasicMeasurementFactory(parameterDao);
    }

    /**
     * A factory to create the measurement validation objects (mvo's) for the BasicMeasurement
     * @return
     */
    @Bean
    public MvoFactory<BasicMeasurementDto, BasicMeasurementMvo> mvoFactory() {
        return new MvoFactory<>(BasicMeasurementMvo.class);
    }

    /**
     * Create the validator
     * @return
     */
    @Bean
    public MeasurementValidator<BasicMeasurementMvo> basicMeasurementValidator() {
        return new MeasurementValidatorImpl<>(this.validator);
    }

    /**
     * Tie it all together in the service bean
     * @return
     */
    @Bean
    public SubmissionService<BasicMeasurementDto, BasicMeasurement, BasicMeasurementMvo> basicSubmissionService() {
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
                basicMeasurementFactory());
    }

    /**
     * The data access object for the Landfill measurement Entity
     * @return
     */
    @Bean
    public MeasurementDao<LandfillMeasurement> landfillMeasurementDao() {
        return new MeasurementDao<>(LandfillMeasurement.class);
    }

    /**
     * The factory to create landfill measurement objects Mvo for validation
     * @return
     */
    @Bean
    public AbstractMeasurementFactory<LandfillMeasurement, LandfillMeasurementDto> landfillMeasurementFactory() {
        return new LandfillMeasurementFactory(
                methodOrStandardDao,
                parameterDao,
                qualifierDao,
                referencePeriodDao,
                returnPeriodDao,
                returnTypeDao,
                siteDao,
                textValueDao,
                uniqueIdentifierAliasDao,
                uniqueIdentifierDao,
                unitDao);
    }

    /**
     * A factory to create the measurement validation objects (mvo's) for the BasicMeasurement
     * @return
     */
    @Bean
    public MvoFactory<LandfillMeasurementDto, LandfillMeasurementMvo> landfillMvoFactory() {
        return new MvoFactory<>(LandfillMeasurementMvo.class);
    }

    /**
     * Create the validator
     * @return
     */
    @Bean
    public MeasurementValidator<LandfillMeasurementMvo> landfillMeasurementValidator() {
        return new MeasurementValidatorImpl<>(this.validator);
    }

    @Bean
    public SubmissionService<LandfillMeasurementDto, LandfillMeasurement, LandfillMeasurementMvo> landfillSubmissionService() {
        return new SubmissionService<>(
                LandfillMeasurementDto.class,
                LandfillMeasurementDto[].class,
                LandfillMeasurement.class,
                landfillMvoFactory(),
                userDao,
                datasetDao,
                recordDao,
                landfillMeasurementDao(),
                landfillMeasurementValidator(),
                landfillMeasurementFactory());
    }

}
