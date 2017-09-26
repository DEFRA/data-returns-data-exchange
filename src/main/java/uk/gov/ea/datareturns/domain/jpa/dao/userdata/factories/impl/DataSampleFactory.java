package uk.gov.ea.datareturns.domain.jpa.dao.userdata.factories.impl;

import org.apache.commons.lang3.StringUtils;
import uk.gov.ea.datareturns.domain.jpa.dao.userdata.factories.AbstractPayloadEntityFactory;
import uk.gov.ea.datareturns.domain.jpa.dao.userdata.factories.TranslationResult;
import uk.gov.ea.datareturns.domain.jpa.entities.masterdata.AliasedEntity;
import uk.gov.ea.datareturns.domain.jpa.entities.masterdata.AliasingEntity;
import uk.gov.ea.datareturns.domain.jpa.entities.masterdata.MasterDataEntity;
import uk.gov.ea.datareturns.domain.jpa.entities.masterdata.impl.*;
import uk.gov.ea.datareturns.domain.jpa.entities.userdata.impl.DataSampleEntity;
import uk.gov.ea.datareturns.domain.jpa.service.MasterDataLookupService;
import uk.gov.ea.datareturns.domain.validation.payloads.datasample.fields.EaId;
import uk.gov.ea.datareturns.domain.validation.payloads.datasample.fields.MonitoringDate;
import uk.gov.ea.datareturns.domain.validation.payloads.datasample.fields.TxtValue;
import uk.gov.ea.datareturns.domain.validation.payloads.datasample.rules.ReturnPeriodFormat;
import uk.gov.ea.datareturns.util.TextUtils;
import uk.gov.ea.datareturns.web.resource.v1.model.record.payload.DataSamplePayload;

import java.math.BigDecimal;
import java.util.function.Consumer;

/**
 * @author Graham Willis
 * Boilerplate to generate instances of the hibernate persistence entity
 */
public class DataSampleFactory extends AbstractPayloadEntityFactory<DataSampleEntity, DataSamplePayload> {
    private MasterDataLookupService lookupSvc;

    public DataSampleFactory(MasterDataLookupService lookupSvc) {
        super(DataSamplePayload.class);
        this.lookupSvc = lookupSvc;
    }

    public TranslationResult<DataSampleEntity> create(DataSamplePayload payload) {
        // Set up an observation with teh entity objects and also logging any substitutions made
        TranslationResult<DataSampleEntity> result = new TranslationResult<>();

        DataSampleEntity dataSampleEntity = new DataSampleEntity();
        result.setEntity(dataSampleEntity);

        // Unique identifier or alias
        UniqueIdentifier eaId = resolve(UniqueIdentifier.class, UniqueIdentifierAlias.class, payload.getEaId(), alias ->
                result.addSubstitution(EaId.FIELD_NAME, alias.getName(), alias.getPreferred().getName())
        );
        dataSampleEntity.setUniqueIdentifier(eaId);
        // Return type
        dataSampleEntity.setReturnType(resolve(ReturnType.class, payload.getReturnType()));

        // Parameter or alias
        Parameter parameter = resolve(Parameter.class, payload.getParameter(), alias ->
                result.addSubstitution(uk.gov.ea.datareturns.domain.validation.payloads.datasample.fields.Parameter.FIELD_NAME,
                        alias.getName(), alias.getPreferred().getName())
        );
        dataSampleEntity.setParameter(parameter);

        // Monitoring point
        dataSampleEntity.setMonPoint(payload.getMonitoringPoint());

        // Monitoring date
        MonitoringDate monDate = new MonitoringDate(payload.getMonitoringDate());
        dataSampleEntity.setMonDate(monDate.getInstant());

        // Return period!
        dataSampleEntity.setReturnPeriod(ReturnPeriodFormat.toStandardisedFormat(payload.getReturnPeriod()));

        // Numeric value
        /*
        TODO: Discuss with Graham as to intention here....
         */
        String value = TextUtils.normalize(payload.getValue(), TextUtils.WhitespaceHandling.REMOVE);
        if (StringUtils.isNotBlank(value)) {
            char firstChar = value.charAt(0);

            if (firstChar == '<' || firstChar == '>') {
                dataSampleEntity.setNumericValueText(String.valueOf(firstChar));
                // Get the remainder after the < or >
                value = value.substring(1);
            }
            try {
                dataSampleEntity.setNumericValue(new BigDecimal(value));
            } catch (NumberFormatException e) {
                dataSampleEntity.setNumericValue(null);
            }
        }

        // Text value or alias
        TextValue textValue = resolve(TextValue.class, payload.getTextValue(), alias ->
                result.addSubstitution(TxtValue.FIELD_NAME, alias.getName(), alias.getPreferred().getName())
        );
        dataSampleEntity.setTextValue(textValue);
        // Qualifier
        dataSampleEntity.setQualifier(resolve(Qualifier.class, payload.getQualifier()));
        // Comments
        dataSampleEntity.setComments(payload.getComments());

        // Units or alias
        Unit unit = resolve(Unit.class, payload.getUnit(), alias ->
                result.addSubstitution(uk.gov.ea.datareturns.domain.validation.payloads.datasample.fields.Unit.FIELD_NAME,
                        alias.getName(), alias.getPreferred().getName())
        );
        dataSampleEntity.setUnit(unit);

        // Method or standard
        dataSampleEntity.setMethodOrStandard(resolve(MethodOrStandard.class, payload.getMethStand()));

        // Reference period or alias
        ReferencePeriod referencePeriod = resolve(ReferencePeriod.class, payload.getReferencePeriod(), alias ->
                result.addSubstitution(uk.gov.ea.datareturns.domain.validation.payloads.datasample.fields.ReferencePeriod.FIELD_NAME,
                        alias.getName(), alias.getPreferred().getName())
        );
        dataSampleEntity.setReferencePeriod(referencePeriod);

        return result;
    }

    private <E extends MasterDataEntity> E resolve(Class<E> entityClass, String value) {
        return lookupSvc.relaxed().find(entityClass, value);
    }

    private <E extends AbstractAliasingEntity<E>> E resolve(Class<E> entityClass, String value, Consumer<E> aliasHandler) {
        E entity = resolve(entityClass, value);
        if (entity != null) {
            if (entity.getPreferred() != null) {
                aliasHandler.accept(entity);
            }
            entity = entity.getPrimary();
        }
        return entity;
    }

    private <E extends AliasedEntity<A>, A extends AliasingEntity<E>> E resolve(Class<E> entityClass, Class<A> aliasClass, String value,
            Consumer<A> aliasHandler) {
        A alias = resolve(aliasClass, value);
        if (alias != null) {
            aliasHandler.accept(alias);
            return alias.getPreferred();
        } else {
            return resolve(entityClass, value);

        }
    }
}
