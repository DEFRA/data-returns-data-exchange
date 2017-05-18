package uk.gov.ea.datareturns.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import uk.gov.ea.datareturns.domain.dto.impl.BasicMeasurementDto;
import uk.gov.ea.datareturns.domain.dto.impl.LandfillMeasurementDto;
import uk.gov.ea.datareturns.domain.jpa.dao.masterdata.*;
import uk.gov.ea.datareturns.domain.jpa.dao.userdata.factories.AbstractObservationFactory;
import uk.gov.ea.datareturns.domain.jpa.dao.userdata.factories.impl.BasicMeasurementFactory;
import uk.gov.ea.datareturns.domain.jpa.dao.userdata.factories.impl.DataSampleFactory;
import uk.gov.ea.datareturns.domain.jpa.dao.userdata.impl.DatasetDao;
import uk.gov.ea.datareturns.domain.jpa.dao.userdata.impl.ObservationDao;
import uk.gov.ea.datareturns.domain.jpa.dao.userdata.impl.RecordDao;
import uk.gov.ea.datareturns.domain.jpa.dao.userdata.impl.UserDao;
import uk.gov.ea.datareturns.domain.jpa.entities.userdata.impl.BasicMeasurement;
import uk.gov.ea.datareturns.domain.jpa.entities.userdata.impl.DataSampleEntity;
import uk.gov.ea.datareturns.domain.jpa.service.SubmissionService;
import uk.gov.ea.datareturns.domain.validation.basicmeasurement.BasicMeasurementFieldMessageMap;
import uk.gov.ea.datareturns.domain.validation.basicmeasurement.BasicMeasurementMvo;
import uk.gov.ea.datareturns.domain.validation.landfillmeasurement.LandfillMeasurementFieldMessageMap;
import uk.gov.ea.datareturns.domain.validation.landfillmeasurement.LandfillMeasurementMvo;
import uk.gov.ea.datareturns.domain.validation.newmodel.validator.MeasurementValidator;
import uk.gov.ea.datareturns.domain.validation.newmodel.validator.MeasurementValidatorImpl;
import uk.gov.ea.datareturns.domain.validation.newmodel.validator.MvoFactory;

import javax.inject.Inject;
import javax.validation.Validator;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

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

    public enum SubmissionServiceProvider {
        BASIC_VERSION_1,
        LANDFILL_VERSION_1,
        DATA_SAMPLE_V1
    }

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
     * Set up the DataSample submission service
     */

    /**
     * The data access object for the BasicMeasurement Entity
     * @return
     */
    @Bean
    public ObservationDao<BasicMeasurement> basicMeasuementDao() {
        return new ObservationDao<>(BasicMeasurement.class);
    }

    /**
     * The record to create Basic measurement objects Mvo for validation
     * @return
     */
    @Bean
    public AbstractObservationFactory<BasicMeasurement, BasicMeasurementDto> basicMeasurementFactory() {
        return new BasicMeasurementFactory(parameterDao);
    }

    /**
     * A record to create the measurement validation objects (mvo's) for the BasicMeasurement
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
        return new MeasurementValidatorImpl<>(this.validator, BasicMeasurementMvo.class, new BasicMeasurementFieldMessageMap());
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
     * The data access object for the data sample observation
     * @return
     */
    @Bean
    public ObservationDao<DataSampleEntity> dataSampleDao() {
        return new ObservationDao<>(DataSampleEntity.class);
    }

    /**
     * The record to create landfill measurement objects Mvo for validation
     * @return
     */
    @Bean
    public AbstractObservationFactory<DataSampleEntity, LandfillMeasurementDto> dataSampleEntityFactory() {
        return new DataSampleFactory(
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
     * A record to create the measurement validation objects (mvo's) for the BasicMeasurement
     * @return
     */
    @Bean
    public MvoFactory<LandfillMeasurementDto, LandfillMeasurementMvo> dataSampleMvoFactory() {
        return new MvoFactory<>(LandfillMeasurementMvo.class);
    }

    /**
     * Create the validator
     * @return
     */
    @Bean
    public MeasurementValidator<LandfillMeasurementMvo> dataSampleValidator() {
        return new MeasurementValidatorImpl<>(this.validator, LandfillMeasurementMvo.class, new LandfillMeasurementFieldMessageMap());
    }

    @Bean
    public SubmissionService<LandfillMeasurementDto, DataSampleEntity, LandfillMeasurementMvo>dataSampleSubmissionService() {
        return new SubmissionService<>(
                LandfillMeasurementDto.class,
                LandfillMeasurementDto[].class,
                DataSampleEntity.class,
                dataSampleMvoFactory(),
                userDao,
                datasetDao,
                recordDao,
                dataSampleDao(),
                dataSampleValidator(),
                dataSampleEntityFactory());
    }

    @Bean(name = "submissionServiceMap")
    public Map<SubmissionServiceProvider, SubmissionService> getSubmissionServiceMap() {
        Map<SubmissionServiceProvider, SubmissionService> map = new HashMap<>();
        map.put(SubmissionServiceProvider.BASIC_VERSION_1, basicSubmissionService());
        map.put(SubmissionServiceProvider.DATA_SAMPLE_V1, dataSampleSubmissionService());
        return Collections.unmodifiableMap(map);
    }

}
