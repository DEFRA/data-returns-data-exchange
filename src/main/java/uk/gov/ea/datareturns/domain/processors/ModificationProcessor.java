package uk.gov.ea.datareturns.domain.processors;

import com.univocity.parsers.annotations.Parsed;
import uk.gov.ea.datareturns.domain.io.csv.generic.CSVModel;
import uk.gov.ea.datareturns.domain.model.DataSample;
import uk.gov.ea.datareturns.domain.model.rules.modifiers.EntityModifier;
import uk.gov.ea.datareturns.domain.model.rules.modifiers.Modifier;

import java.beans.IntrospectionException;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.io.IOException;
import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;


/**
 * Created by graham on 19/08/16.
 */
public class ModificationProcessor {

    private CSVModel<DataSample> csvInput;

    private class ModificationTask {
        protected Class<? extends EntityModifier> modifier;
        protected String header;
        protected Method readMethod;
        protected Method writeMethod;

        public ModificationTask(String header, Class<? extends EntityModifier> modifier, Method readMethod, Method writeMethod) {
            this.header = header;
            this.modifier = modifier;
            this.readMethod = readMethod;
            this.writeMethod = writeMethod;
        }
    }

    private Map<String, ModificationTask> modificationTaskList  = new HashMap<>();

    public ModificationProcessor(CSVModel<DataSample> csvInput) throws IOException {
        this.csvInput = csvInput;
        PropertyDescriptor[] propertyDescriptors = null;

        // Calculate all of the modification tasks by reading the annotations on DataSample.
        // There must be a @Parsed annotation identifying the header and
        // a @Modifier annotation identifying the modification processor
        try {
            propertyDescriptors = Introspector.getBeanInfo(DataSample.class).getPropertyDescriptors();
        } catch (IntrospectionException e) {
            throw new IOException(e.getMessage());
        }

        Field[] declaredFields = DataSample.class.getDeclaredFields();
        for (Field field : declaredFields) {
            Parsed parsed = null;
            Modifier modifier = null;
            Annotation[] fieldAnnotations = field.getAnnotations();
            for (Annotation annotation : fieldAnnotations) {
                parsed = field.getAnnotation(Parsed.class) == null ? parsed : field.getAnnotation(Parsed.class);
                modifier = field.getAnnotation(Modifier.class) == null ? modifier : field.getAnnotation(Modifier.class);
            }
            if (parsed != null && modifier != null) {
                for(PropertyDescriptor propertyDescriptor : propertyDescriptors) {
                    if (propertyDescriptor.getName().equals(field.getName())) {
                        modificationTaskList.put(field.getName(),
                                new ModificationTask(parsed.field(), modifier.modifier(),
                                        propertyDescriptor.getReadMethod(), propertyDescriptor.getWriteMethod()
                                )
                        );
                    }
                }
            }
        }
    }

    public CSVModel<DataSample> performSubstitutions() {
        for (DataSample row : csvInput.getRecords()) {
            // Make all the modifications in modificationTaskList
            for (Map.Entry<String, ModificationTask> es : modificationTaskList.entrySet()) {
                ModificationTask modificationTask = es.getValue();
                try {
                    Class<? extends EntityModifier> em = modificationTask.modifier;
                    Object read = modificationTask.readMethod.invoke(row);
                    Object write = em.doModify(read);
                    modificationTask.writeMethod.invoke(row, write);
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                } catch (InvocationTargetException e) {
                    e.printStackTrace();
                }
            }
        }
        return null;
    }
}
