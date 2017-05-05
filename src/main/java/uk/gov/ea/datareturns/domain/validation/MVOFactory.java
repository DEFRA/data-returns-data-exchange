package uk.gov.ea.datareturns.domain.validation;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import uk.gov.ea.datareturns.domain.dto.MeasurementDto;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;

/**
 * @author Graham Willis
 * Initiated from the data transport object (deserialized JSON)
 * and responsible for defining the specific validations applicabe for a given measurement
 * type
 */
public class MVOFactory<D extends MeasurementDto, V extends MVO> {
    protected static final Logger LOGGER = LoggerFactory.getLogger(MVOFactory.class);

    private final Class<V> mvoClass;
    private static MVOFactory mvoFactory;

    public MVOFactory(Class<V> mvoClass) {
        this.mvoClass = mvoClass;
    }

    public V create(D dto) {
        try {
            Constructor ctor = mvoClass.getDeclaredConstructor(dto.getClass());
            ctor.setAccessible(true);
            V instance = (V)ctor.newInstance(dto);
            return instance;
        } catch (NoSuchMethodException|IllegalAccessException|InstantiationException|InvocationTargetException e) {
            LOGGER.error("Cannot create measurement validation object for: " + dto.toString());
            return null;
        }
    }
}
