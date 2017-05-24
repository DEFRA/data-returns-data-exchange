package uk.gov.ea.datareturns.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import uk.gov.ea.datareturns.domain.jpa.dao.masterdata.*;
import uk.gov.ea.datareturns.domain.jpa.dao.userdata.factories.AbstractObservationFactory;
import uk.gov.ea.datareturns.domain.jpa.dao.userdata.factories.impl.DataSampleFactory;
import uk.gov.ea.datareturns.domain.jpa.dao.userdata.impl.*;
import uk.gov.ea.datareturns.domain.jpa.entities.userdata.impl.DataSampleEntity;
import uk.gov.ea.datareturns.domain.jpa.service.DatasetService;
import uk.gov.ea.datareturns.domain.jpa.service.SubmissionService;
import uk.gov.ea.datareturns.domain.validation.datasample.DataSampleMvo;
import uk.gov.ea.datareturns.domain.validation.newmodel.validator.*;
import uk.gov.ea.datareturns.web.resource.v1.model.record.payload.DataSamplePayload;

import javax.inject.Inject;
import javax.validation.Validator;

/**
 * @author Graham Willis
 */
@Configuration
public class SubmissionConfiguration {

    private final Validator validator;
    private final ValidationErrorDao validationErrorDao;
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
            ValidationErrorDao validationErrorDao,
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
        this.validationErrorDao = validationErrorDao;
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
     * Create the data access object Dao for database persistence of the DataSampleEntity class
     * @return
     */
    @Bean
    public ObservationDao observationDao() {
        return new ObservationDao();
    }

    /**
     * The record to create landfill measurement objects Mvo for validation
     * @return
     */
    @Bean
    public AbstractObservationFactory<DataSampleEntity, DataSamplePayload> dataSampleEntityFactory() {
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
     * Create the validator
     * @return
     */
    @Bean
    public ObservationValidator<DataSampleMvo> dataSampleValidator() {
        return new ObservationValidatorImpl<>(this.validator, validationErrorDao);
    }

    @Bean
    public DatasetService datasetService() {
        return new DatasetService(userDao, datasetDao);
    }

    @Bean
    public ObservationValidator<Mvo> mvoValidator() {
        return new MvoValidator(this.validator, validationErrorDao);
    }

    @Bean
    public NewMvoFactory newMvoFactory() {
        return new NewMvoFactory();
    }

    @Bean
    public SubmissionService submissionsService() {
        return new SubmissionService(newMvoFactory(), recordDao, observationDao(), mvoValidator());
    }
}
