package uk.gov.ea.datareturns.domain.model.rules;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import uk.gov.ea.datareturns.domain.model.fields.FieldValue;
import uk.gov.ea.datareturns.domain.model.fields.MappedField;

import java.beans.IntrospectionException;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

/**
 * The {@link FieldMapping} class provides the necessary information to map between a field name in a file and a JavaBean property.
 */
public class FieldMapping {
    /** Class logger */
    private static final Logger LOGGER = LoggerFactory.getLogger(FieldMapping.class);
    /** Java Field reference */
    private final Field field;
    /** MappedField annotation for the JavaBean field */
    private final MappedField mappedField;
    /** PropertyDescriptor information relating to the JavaBean field */
    private final PropertyDescriptor descriptor;

    /**
     * Create a new FieldMapping
     *
     * @param descriptor the {@link PropertyDescriptor} for the mapped field
     * @param field the reference to the {@link Field} on the JavaBean
     * @param mappedField the {@link MappedField} annotation associated with the JavaBean field
     */
    private FieldMapping(PropertyDescriptor descriptor, Field field, MappedField mappedField) {
        this.field = field;
        this.mappedField = mappedField;
        this.descriptor = descriptor;
    }

    /**
     * @return a reference to the {@link Field} on the JavaBean for this mapping
     */
    public Field getField() {
        return field;
    }

    /**
     * @return a reference to the {@link MappedField} annotation on the JavaBean for this mapping
     */
    public MappedField getMappedField() {
        return mappedField;
    }

    /**
     * @return the Object type of the JavaBean field for this mapping
     */
    public Class<?> getType() {
        return descriptor.getPropertyType();
    }

    /**
     * Gets output value.
     *
     * @param target the target
     * @return the output value
     */
    public String getOutputValue(final Object target) {
        Object o = null;
        try {
            o = descriptor.getReadMethod().invoke(target);
            // TODO: Separate implementation from the FieldValue class
            if (o != null && FieldValue.class.isAssignableFrom(getType())) {
                return ((FieldValue) o).transform(target);
            }
        } catch (ReflectiveOperationException e) {
            LOGGER.error("Unable to retrieve output value", e);
        }
        return Objects.toString(o, "");
    }

    /**
     * Gets the value the user input
     * @param target
     * @return
     */
    public String getInputValue(final Object target) {
        Object o = null;
        try {
            o = descriptor.getReadMethod().invoke(target);
            if (o != null && FieldValue.class.isAssignableFrom(getType())) {
                return ((FieldValue) o).getInputValue();
            }
        } catch (ReflectiveOperationException e) {
            LOGGER.error("Unable to retrieve input value", e);
        }
        return Objects.toString(o, "");
    }

    /**
     * Sets value.
     *
     * @param target the target
     * @param inputValue the input value
     */
    public void setValue(final Object target, String inputValue) {
        try {
            final Constructor<?> constructor = getType().getConstructor(String.class);
            final Object fv = constructor.newInstance(inputValue);
            descriptor.getWriteMethod().invoke(target, fv);
        } catch (NoSuchMethodException e) {
            throw new RuntimeException(
                    "Unable to find constructor with a single String argument for class " + mappedField.value().getType(), e);
        } catch (ReflectiveOperationException e) {
            throw new RuntimeException(
                    "Error attempting to instantiate model entity for class " + mappedField.value().getType(), e);
        }
    }

    /**
     * Retrieve a mapping of a field name (used in a file) to details of the JavaBean property which is mapped for the field name.
     *
     * @param beanClass the {@link Class} to examine to determine mappings
     * @return a {@link Map} of {@link String} (field name) to {@link FieldMapping} (JavaBean property information)
     */
    public static Map<String, FieldMapping> getFieldNameToBeanMap(final Class<?> beanClass) {
        final Map<String, FieldMapping> mappings = new HashMap<>();
        try {
            for (final PropertyDescriptor pd : Introspector.getBeanInfo(beanClass).getPropertyDescriptors()) {
                final String property = pd.getName();
                Field field;
                try {
                    field = beanClass.getDeclaredField(property);
                } catch (NoSuchFieldException e) {
                    continue;
                }

                final MappedField mappedField = field.getAnnotation(MappedField.class);
                if (mappedField == null) {
                    continue;
                }
                if (pd.getReadMethod() == null || pd.getWriteMethod() == null) {
                    LOGGER.warn("Ignoring field " + property
                            + ".  All mappable fields must have accessor methods as per the JavaBean specification.");
                    continue;
                }
                mappings.put(mappedField.value().getName(), new FieldMapping(pd, field, mappedField));
            }
        } catch (final IntrospectionException e) {
            LOGGER.error("Unable to inspect bean mappedField", e);
        }
        return mappings;
    }

    /**
     * Retrieve a mapping of JavaBean property name to serialized field name (used in a file)
     *
     * @param beanClass the {@link Class} to examine to determine mappings
     * @return a {@link Map} of {@link String} (Javabean property name) to {@link String} (field name)
     */
    public static Map<String, String> getBeanToFieldNameMap(final Class<?> beanClass) {
        Map<String, FieldMapping> fieldNameToBeanMap = getFieldNameToBeanMap(beanClass);
        Map<String, String> mappings = new HashMap<>();
        fieldNameToBeanMap.forEach((k, v) -> mappings.put(v.field.getName(), k));
        return mappings;
    }

}
