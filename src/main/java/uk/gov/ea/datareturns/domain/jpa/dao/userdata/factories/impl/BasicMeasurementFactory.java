package uk.gov.ea.datareturns.domain.jpa.dao.userdata.factories.impl;

import uk.gov.ea.datareturns.domain.dto.impl.BasicMeasurementDto;
import uk.gov.ea.datareturns.domain.jpa.dao.masterdata.Key;
import uk.gov.ea.datareturns.domain.jpa.dao.masterdata.ParameterDao;
import uk.gov.ea.datareturns.domain.jpa.dao.userdata.factories.AbstractObservationFactory;
import uk.gov.ea.datareturns.domain.jpa.entities.userdata.impl.BasicMeasurement;
import uk.gov.ea.datareturns.web.resource.v1.model.record.payload.DataSamplePayload;
import uk.gov.ea.datareturns.web.resource.v1.model.record.payload.DemonstrationAlternativePayload;

import java.math.BigDecimal;

/**
 * @author Graham Willis
 * Used to generate instances of the hibernate persistence entity
 */
public class BasicMeasurementFactory extends AbstractObservationFactory<BasicMeasurement, DemonstrationAlternativePayload> {

    private final ParameterDao parameterDao;

    public BasicMeasurementFactory(ParameterDao parameterDao) {
        super(DemonstrationAlternativePayload.class);
        this.parameterDao = parameterDao;
    }

    public BasicMeasurement create(BasicMeasurementDto dto) {
        BasicMeasurement basicMeasurement = new BasicMeasurement();

        basicMeasurement.setParameter(parameterDao.getByNameOrAlias(Key.relaxed(dto.getParameter())));
        basicMeasurement.setNumericValue(new BigDecimal(dto.getValue()));

        return basicMeasurement;
    }

    @Override
    public BasicMeasurement create(DemonstrationAlternativePayload payload) {
        return null;
    }
}
