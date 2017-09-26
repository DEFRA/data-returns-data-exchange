package uk.gov.ea.datareturns.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import uk.gov.ea.datareturns.domain.jpa.dao.userdata.factories.AbstractPayloadEntityFactory;
import uk.gov.ea.datareturns.domain.jpa.dao.userdata.factories.impl.AlternativeFactory;
import uk.gov.ea.datareturns.domain.jpa.dao.userdata.factories.impl.DataSampleFactory;
import uk.gov.ea.datareturns.domain.jpa.dao.userdata.impl.*;
import uk.gov.ea.datareturns.domain.jpa.entities.userdata.impl.AlternativePayload;
import uk.gov.ea.datareturns.domain.jpa.entities.userdata.impl.DataSampleEntity;
import uk.gov.ea.datareturns.domain.jpa.repositories.systemdata.PayloadTypeRepository;
import uk.gov.ea.datareturns.domain.jpa.repositories.systemdata.ValidationConstraintRepository;
import uk.gov.ea.datareturns.domain.jpa.repositories.userdata.DatasetRepository;
import uk.gov.ea.datareturns.domain.jpa.repositories.userdata.RecordRepository;
import uk.gov.ea.datareturns.domain.jpa.service.MasterDataLookupService;
import uk.gov.ea.datareturns.domain.jpa.service.SubmissionService;
import uk.gov.ea.datareturns.domain.validation.common.validator.AbstractValidationObject;
import uk.gov.ea.datareturns.domain.validation.common.validator.ValidationObjectFactory;
import uk.gov.ea.datareturns.domain.validation.common.validator.Validator;
import uk.gov.ea.datareturns.domain.validation.common.validator.ValidatorImpl;
import uk.gov.ea.datareturns.web.resource.v1.model.record.payload.DataSamplePayload;
import uk.gov.ea.datareturns.web.resource.v1.model.record.payload.DemonstrationAlternativePayload;

import javax.inject.Inject;

/**
 * @author Graham Willis
 */
@Configuration
public class SubmissionConfiguration {

    private final javax.validation.Validator validator;
    private final ValidationConstraintRepository validationConstraintRepository;

    private final DatasetRepository datasetRepository;
    private final RecordRepository recordRepository;
    private final PayloadTypeRepository payloadTypeRepository;

    @Inject
    public SubmissionConfiguration(
            javax.validation.Validator validator,
            ValidationConstraintRepository validationConstraintRepository,
            PayloadTypeRepository payloadTypeRepository,
            DatasetRepository datasetRepository,
            RecordRepository recordRepository
    ) {
        this.validator = validator;
        this.validationConstraintRepository = validationConstraintRepository;
        this.payloadTypeRepository = payloadTypeRepository;
        this.datasetRepository = datasetRepository;
        this.recordRepository = recordRepository;
    }

    /**
     * Create the data access object Dao for database persistence of the DataSampleEntity class
     * @return
     */
    @Bean
    public PayloadEntityDao payloadEntityDao() {
        return new PayloadEntityDao();
    }

    /**
     * The record to create landfill measurement objects AbstractValidationObject for validation
     * @return
     */
    @Bean
    public AbstractPayloadEntityFactory<DataSampleEntity, DataSamplePayload> dataSampleEntityFactory(MasterDataLookupService lookupService) {
        return new DataSampleFactory(lookupService);
    }

    @Bean
    public AbstractPayloadEntityFactory<AlternativePayload, DemonstrationAlternativePayload> alternativeFactory() {
        return new AlternativeFactory();
    }

    @Bean
    public Validator<AbstractValidationObject> validationObjectValidator() {
        return new ValidatorImpl(this.validator, validationConstraintRepository, payloadTypeRepository);
    }

    @Bean
    public ValidationObjectFactory validationObjectFactory() {
        return new ValidationObjectFactory();
    }

    @Bean
    public SubmissionService submissionsService() {
        return new SubmissionService(validationObjectFactory(), datasetRepository, recordRepository, payloadEntityDao(), validationObjectValidator());
    }
}
