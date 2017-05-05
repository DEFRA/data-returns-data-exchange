package uk.gov.ea.datareturns.domain.jpa.entities.userdata.impl;

import uk.gov.ea.datareturns.domain.dto.impl.BasicMeasurementDto;
import uk.gov.ea.datareturns.domain.jpa.dao.masterdata.Key;
import uk.gov.ea.datareturns.domain.jpa.dao.masterdata.ParameterDao;
import uk.gov.ea.datareturns.domain.jpa.entities.userdata.AbstractMeasurementFactory;

import javax.inject.Inject;

/**
 * @author Graham Willis
 * Used to generate instances of the hibernate persistence entity
 */
public class BasicMeasurementFactory implements AbstractMeasurementFactory<BasicMeasurement, BasicMeasurementDto> {

    private final ParameterDao parameterDao;

    @Inject
    public BasicMeasurementFactory(ParameterDao parameterDao) {
        this.parameterDao = parameterDao;
    }

    @Override
    public BasicMeasurement create(BasicMeasurementDto dto) {
        BasicMeasurement basicMeasurement = new BasicMeasurement();

        basicMeasurement.setParameter(parameterDao.getByNameOrAlias(Key.relaxed(dto.getParameter())));
        basicMeasurement.setNumericValue(dto.getValue());

        return basicMeasurement;
    }
}
