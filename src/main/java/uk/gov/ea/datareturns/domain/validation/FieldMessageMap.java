package uk.gov.ea.datareturns.domain.validation;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import uk.gov.ea.datareturns.domain.validation.model.fields.FieldValue;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Type;
import java.util.*;

/**
 * @author Graham Willis
 */
public abstract class FieldMessageMap<V extends Mvo> {

    private static final Logger LOGGER = LoggerFactory.getLogger(FieldMessageMap.class);
    private static Map<Type, Method> typeMethodMap = new HashMap<>();
    private final static Map<String, List<Class<? extends FieldValue>>> messageFieldsMap = new HashMap<>();

    protected FieldMessageMap(Class<V> measurementMvoClass) {
        for (Method method : measurementMvoClass.getDeclaredMethods()) {
            Type returnType = method.getGenericReturnType();
            typeMethodMap.put(returnType, method);
        }
    }

    protected void add(String key, Class<? extends FieldValue<?>> ... values) {
        if (messageFieldsMap.containsKey(key)) {
            LOGGER.error("Initialization error: " + key + " already exists in messageFieldsMap");
        } else {
            messageFieldsMap.put(key, Arrays.asList(values));
        }
    }

    /**
     * Relates an error message to a set of FieldValue items which are added to the error validation message
     * as part of the validation.
     * @param message
     * @return
     */
    public List<FieldValue<?>> getFieldDependencies(V measurement, String message) {
        List<FieldValue<?>> result = new ArrayList<>();
        List<Class<? extends FieldValue>> fieldClasses = messageFieldsMap.get(message);
        for (Class<? extends FieldValue> fieldClass : fieldClasses) {
            Method getter = typeMethodMap.get(fieldClass);
            try {
                FieldValue<?> field = (FieldValue<?>) getter.invoke(measurement);
                result.add(field);
            } catch (IllegalAccessException|InvocationTargetException e) {
                LOGGER.error("Could not invoke a getter: " + fieldClass.getCanonicalName());
            }
        }
        return result;
    }

}
