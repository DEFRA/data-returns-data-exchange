package uk.gov.ea.datareturns.domain.jpa.dao.userdata.factories.impl;

import uk.gov.ea.datareturns.domain.dto.impl.LandfillMeasurementDto;
import uk.gov.ea.datareturns.domain.jpa.dao.masterdata.*;
import uk.gov.ea.datareturns.domain.jpa.dao.userdata.factories.AbstractObservationFactory;
import uk.gov.ea.datareturns.domain.jpa.entities.userdata.impl.DataSampleEntity;
import uk.gov.ea.datareturns.domain.validation.model.fields.impl.ds.MonitoringDate;

import java.math.BigDecimal;

/**
 * @author Graham Willis
 * Boilerplate to generate instances of the hibernate persistence entity
 */
public class DataSampleFactory implements AbstractObservationFactory<DataSampleEntity, LandfillMeasurementDto> {

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

    @Override
    public DataSampleEntity create(LandfillMeasurementDto dto) {
        DataSampleEntity measurement = new DataSampleEntity();

        measurement.setUniqueIdentifier(uniqueIdentifierDao.getByNameOrAlias(Key.relaxed(dto.getEaId())));
        measurement.setSite(siteDao.getByName((Key.relaxed(dto.getSiteName()))));
        measurement.setReturnType(returnTypeDao.getByName(Key.relaxed((dto.getReturnType()))));
        MonitoringDate monDate = new MonitoringDate(dto.getMonitoringDate());
        measurement.setMonDate(monDate.getInstant());
        measurement.setMonPoint(dto.getMonitoringPoint());
        measurement.setParameter(parameterDao.getByNameOrAlias(Key.relaxed(dto.getParameter())));
        measurement.setNumericValue(new BigDecimal(dto.getValue()));
        measurement.setTextValue(textValueDao.getByName(Key.relaxed((dto.getTextValue()))));
        measurement.setQualifier(qualifierDao.getByName(Key.relaxed((dto.getQualifier()))));
        measurement.setUnit(unitDao.getByName(Key.relaxed((dto.getUnit()))));

        return measurement;
    }
}
