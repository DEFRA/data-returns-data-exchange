package uk.gov.ea.datareturns.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import uk.gov.ea.datareturns.domain.jpa.dao.masterdata.*;
import uk.gov.ea.datareturns.domain.jpa.dao.userdata.factories.AbstractPayloadEntityFactory;
import uk.gov.ea.datareturns.domain.jpa.dao.userdata.factories.impl.AlternativeFactory;
import uk.gov.ea.datareturns.domain.jpa.dao.userdata.factories.impl.DataSampleFactory;
import uk.gov.ea.datareturns.domain.jpa.dao.userdata.impl.*;
import uk.gov.ea.datareturns.domain.jpa.entities.userdata.impl.AlternativePayload;
import uk.gov.ea.datareturns.domain.jpa.entities.userdata.impl.DataSampleEntity;
import uk.gov.ea.datareturns.domain.jpa.service.DatasetService;
import uk.gov.ea.datareturns.domain.jpa.service.SubmissionService;
import uk.gov.ea.datareturns.domain.validation.newmodel.validator.*;
import uk.gov.ea.datareturns.web.resource.v1.model.record.payload.DataSamplePayload;
import uk.gov.ea.datareturns.web.resource.v1.model.record.payload.DemonstrationAlternativePayload;

import javax.inject.Inject;

/**
 * @author Graham Willis
 */
@Configuration
public class SubmissionConfiguration {

    private final javax.validation.Validator validator;
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

    @Inject
    public SubmissionConfiguration(
            javax.validation.Validator validator,
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
    public PayloadEntityDao observationDao() {
        return new PayloadEntityDao();
    }

    /**
     * The record to create landfill measurement objects AbstractValidationObject for validation
     * @return
     */
    @Bean
    public AbstractPayloadEntityFactory<DataSampleEntity, DataSamplePayload> dataSampleEntityFactory() {
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

    @Bean
    public AbstractPayloadEntityFactory<AlternativePayload, DemonstrationAlternativePayload> alternativeFactory() {
        return new AlternativeFactory();
    }

    @Bean
    public DatasetService datasetService() {
        return new DatasetService(userDao, datasetDao);
    }

    @Bean
    public Validator<AbstractValidationObject> mvoValidator() {
        return new ValidatorImpl(this.validator, validationErrorDao);
    }

    @Bean
    public ValidationObjectFactory newMvoFactory() {
        return new ValidationObjectFactory();
    }

    @Bean
    public SubmissionService submissionsService() {
        return new SubmissionService(newMvoFactory(), recordDao, observationDao(), mvoValidator());
    }
}
