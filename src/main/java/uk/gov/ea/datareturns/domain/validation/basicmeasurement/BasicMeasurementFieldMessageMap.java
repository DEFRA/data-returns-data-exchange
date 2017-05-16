package uk.gov.ea.datareturns.domain.validation.basicmeasurement;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import uk.gov.ea.datareturns.domain.validation.FieldMessageMap;
import uk.gov.ea.datareturns.domain.validation.landfillmeasurement.LandfillMeasurementFieldMessageMap;
import uk.gov.ea.datareturns.domain.validation.landfillmeasurement.LandfillMeasurementMvo;
import uk.gov.ea.datareturns.domain.validation.landfillmeasurement.fields.*;
import uk.gov.ea.datareturns.domain.validation.model.fields.FieldValue;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * The field mapping for landfill measurements
 */
public class BasicMeasurementFieldMessageMap implements FieldMessageMap<BasicMeasurementMvo> {

    private static final Logger LOGGER = LoggerFactory.getLogger(BasicMeasurementFieldMessageMap.class);
    private static Class<BasicMeasurementMvo> measurementMvoClass = BasicMeasurementMvo.class;

    @Override
    public List<FieldValue<BasicMeasurementMvo, ?>> getFieldDependencies(BasicMeasurementMvo measurement, String message) {
        //PropertyDescriptor pd = BeanUtils.getPropertyDescriptor(measurementMvoClass, property);
        //Method getter = pd.getReadMethod();
        return null;
    }
}
