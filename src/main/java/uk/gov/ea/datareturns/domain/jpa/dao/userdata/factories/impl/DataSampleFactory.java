package uk.gov.ea.datareturns.domain.jpa.dao.userdata.factories.impl;

import uk.gov.ea.datareturns.domain.jpa.dao.masterdata.*;
import uk.gov.ea.datareturns.domain.jpa.dao.userdata.factories.AbstractPayloadEntityFactory;
import uk.gov.ea.datareturns.domain.jpa.entities.masterdata.impl.Parameter;
import uk.gov.ea.datareturns.domain.jpa.entities.masterdata.impl.TextValue;
import uk.gov.ea.datareturns.domain.jpa.entities.masterdata.impl.UniqueIdentifierAlias;
import uk.gov.ea.datareturns.domain.jpa.entities.masterdata.impl.Unit;
import uk.gov.ea.datareturns.domain.jpa.entities.userdata.impl.DataSampleEntity;
import uk.gov.ea.datareturns.domain.validation.datasample.fields.EaId;
import uk.gov.ea.datareturns.domain.validation.datasample.fields.MonitoringDate;
import uk.gov.ea.datareturns.web.resource.v1.model.record.payload.DataSamplePayload;

import java.math.BigDecimal;

/**
 * @author Graham Willis
 * Boilerplate to generate instances of the hibernate persistence entity
 */
public class DataSampleFactory extends AbstractPayloadEntityFactory<DataSampleEntity, DataSamplePayload> {

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

    public DataSampleFactory(
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
        super(DataSamplePayload.class);

        this.methodOrStandardDao = methodOrStandardDao;
        this.parameterDao = parameterDao;
        this.qualifierDao = qualifierDao;
        this.referencePeriodDao = referencePeriodDao;
        this.returnTypeDao = returnTypeDao;
        this.returnPeriodDao = returnPeriodDao;
        this.siteDao = siteDao;
        this.textValueDao = textValueDao;
        this.uniqueIdentifierAliasDao = uniqueIdentifierAliasDao;
        this.uniqueIdentifierDao = uniqueIdentifierDao;
        this.unitDao = unitDao;
    }

    public DataSampleEntity create(DataSamplePayload payload) {
        // Set up an observation with teh entity objects and also logging any substitutions made
        DataSampleEntity dataSampleEntity = new DataSampleEntity();

        // Unique identifier or alias
        UniqueIdentifierAlias uniqueIdentifierAlias = uniqueIdentifierAliasDao.getByName(Key.relaxed(payload.getEaId()));
        if (uniqueIdentifierAlias != null) {
            dataSampleEntity.setUniqueIdentifier(uniqueIdentifierAlias.getUniqueIdentifier());
            dataSampleEntity.addSubstution(EaId.FIELD_NAME, uniqueIdentifierAlias.getName(), uniqueIdentifierAlias.getUniqueIdentifier().getName());
        } else {
            dataSampleEntity.setUniqueIdentifier(uniqueIdentifierDao.getByNameOrAlias(Key.relaxed(payload.getEaId())));
        }

        // Site name
        dataSampleEntity.setSite(siteDao.getByName((Key.relaxed(payload.getSiteName()))));

        // Return type
        dataSampleEntity.setReturnType(returnTypeDao.getByName(Key.relaxed((payload.getReturnType()))));

        // Parameter or alias
        Parameter parameter = parameterDao.getByAliasName(Key.relaxed(payload.getParameter()));
        if (parameter != null) {
            dataSampleEntity.setParameter(parameter);
            dataSampleEntity.addSubstution(uk.gov.ea.datareturns.domain.validation.datasample.fields.Parameter.FIELD_NAME, parameter.getName(), parameter.getPreferred());
        } else {
            dataSampleEntity.setParameter(parameterDao.getByName(Key.relaxed(payload.getParameter())));
        }

        // Monitoring point
        dataSampleEntity.setMonPoint(payload.getMonitoringPoint());

        // Monitoring date
        MonitoringDate monDate = new MonitoringDate(payload.getMonitoringDate());
        dataSampleEntity.setMonDate(monDate.getInstant());

        // Numeric value
        dataSampleEntity.setNumericValue(new BigDecimal(payload.getValue()));

        // Text value or alias
        TextValue textValueAlias = textValueDao.getByAliasName(Key.relaxed(payload.getTextValue()));
        if (textValueAlias != null) {
            dataSampleEntity.setTextValue(textValueAlias);
            dataSampleEntity.addSubstution(uk.gov.ea.datareturns.domain.validation.datasample.fields.TxtValue.FIELD_NAME, textValueAlias.getName(), textValueAlias.getPreferred());
        } else {
            dataSampleEntity.setTextValue(textValueDao.getByName(Key.relaxed(payload.getTextValue())));
        }

        // Qualifier
        dataSampleEntity.setQualifier(qualifierDao.getByName(Key.relaxed((payload.getQualifier()))));

        // Comments
        dataSampleEntity.setComments(payload.getComments());

        // Units or alias
        Unit unitAlias = unitDao.getByAliasName(Key.relaxed((payload.getUnit())));
        if (unitAlias != null) {
            dataSampleEntity.setUnit(unitAlias);
            dataSampleEntity.addSubstution(uk.gov.ea.datareturns.domain.validation.datasample.fields.Unit.FIELD_NAME, unitAlias.getName(), unitAlias.getPreferred());
        } else {
            dataSampleEntity.setUnit(unitDao.getByName(Key.relaxed((payload.getUnit()))));
        }

        return dataSampleEntity;
    }

}
