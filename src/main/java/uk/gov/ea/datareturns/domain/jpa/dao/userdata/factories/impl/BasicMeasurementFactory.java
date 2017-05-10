package uk.gov.ea.datareturns.domain.jpa.dao.userdata.factories.impl;

import uk.gov.ea.datareturns.domain.dto.impl.BasicMeasurementDto;
import uk.gov.ea.datareturns.domain.jpa.dao.masterdata.Key;
import uk.gov.ea.datareturns.domain.jpa.dao.masterdata.ParameterDao;
import uk.gov.ea.datareturns.domain.jpa.dao.userdata.factories.AbstractMeasurementFactory;
import uk.gov.ea.datareturns.domain.jpa.entities.userdata.impl.BasicMeasurement;

import java.math.BigDecimal;

/**
 * @author Graham Willis
 * Used to generate instances of the hibernate persistence entity
 */
public class BasicMeasurementFactory implements AbstractMeasurementFactory<BasicMeasurement, BasicMeasurementDto> {

    private final ParameterDao parameterDao;

    public BasicMeasurementFactory(ParameterDao parameterDao) {
        this.parameterDao = parameterDao;
    }

    @Override
    public BasicMeasurement create(BasicMeasurementDto dto) {
        BasicMeasurement basicMeasurement = new BasicMeasurement();

        basicMeasurement.setParameter(parameterDao.getByNameOrAlias(Key.relaxed(dto.getParameter())));
        basicMeasurement.setNumericValue(new BigDecimal(dto.getValue()));

        return basicMeasurement;
    }
}
