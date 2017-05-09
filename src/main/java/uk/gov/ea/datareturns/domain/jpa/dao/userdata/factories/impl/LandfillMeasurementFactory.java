package uk.gov.ea.datareturns.domain.jpa.dao.userdata.factories.impl;

import uk.gov.ea.datareturns.domain.dto.impl.LandfillMeasurementDto;
import uk.gov.ea.datareturns.domain.jpa.dao.masterdata.*;
import uk.gov.ea.datareturns.domain.jpa.dao.userdata.factories.AbstractMeasurementFactory;
import uk.gov.ea.datareturns.domain.jpa.entities.userdata.impl.LandfillMeasurement;
import uk.gov.ea.datareturns.domain.model.fields.impl.MonitoringDate;

import java.util.Date;

/**
 * @author Graham Willis
 * Boilerplate to generate instances of the hibernate persistence entity
 */
public class LandfillMeasurementFactory implements AbstractMeasurementFactory<LandfillMeasurement, LandfillMeasurementDto> {

    private MethodOrStandardDao methodOrStandardDao;
    private ParameterDao parameterDao;
    private QualifierDao qualifierDao;
    private ReferencePeriodDao referencePeriodDao;
    private ReturnPeriodDao returnPeriodDao;
    private ReturnTypeDao returnTypeDao;
    private SiteDao siteDao;
    private TextValueDao textValueDao;
    private UniqueIdentifierAliasDao uniqueIdentifierAliasDao;
    private UniqueIdentifierDao uniqueIdentifierDao;
    private UnitDao unitDao;

    public LandfillMeasurementFactory(
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
    public LandfillMeasurement create(LandfillMeasurementDto dto) {
        LandfillMeasurement measurement = new LandfillMeasurement();

        measurement.setUniqueIdentifier(uniqueIdentifierDao.getByNameOrAlias(Key.relaxed(dto.getEaId())));
        measurement.setSite(siteDao.getByName((Key.relaxed(dto.getSiteName()))));
        measurement.setReturnType(returnTypeDao.getByName(Key.relaxed((dto.getReturnType()))));
        MonitoringDate monDate = new MonitoringDate(dto.getMonitoringDate());
        measurement.setMonDate(Date.from(monDate.getInstant()));
        measurement.setMonPoint(dto.getMonitoringPoint());
        measurement.setParameter(parameterDao.getByNameOrAlias(Key.relaxed(dto.getParameter())));
        measurement.setNumericValue(dto.getValue());
        measurement.setTextValue(textValueDao.getByName(Key.relaxed((dto.getTextValue()))));
        measurement.setQualifier(qualifierDao.getByName(Key.relaxed((dto.getQualifier()))));
        measurement.setUnit(unitDao.getByName(Key.relaxed((dto.getUnit()))));

        return measurement;
    }
}
