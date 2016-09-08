package uk.gov.ea.datareturns.domain.processors;

import com.univocity.parsers.annotations.Parsed;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Component;
import uk.gov.ea.datareturns.domain.io.csv.generic.AbstractCSVRecord;
import uk.gov.ea.datareturns.domain.io.csv.generic.CSVModel;
import uk.gov.ea.datareturns.domain.model.rules.modifiers.field.EntityModifier;
import uk.gov.ea.datareturns.domain.model.rules.modifiers.field.Modifier;
import uk.gov.ea.datareturns.domain.model.rules.modifiers.record.RecordModifier;

import java.beans.IntrospectionException;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.io.IOException;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by graham on 19/08/16.
 */
@Component
public class ModificationProcessor<T extends AbstractCSVRecord> implements ApplicationContextAware {
    private static final Logger LOGGER = LoggerFactory.getLogger(ModificationProcessor.class);
    private ApplicationContext applicationContext;

    private CSVModel<T> csvInput;

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
    }

    private class FieldModificationTask {
        protected final Class<? extends EntityModifier> modifierClass;
        protected final String header;
        protected final Method readMethod;
        protected final Method writeMethod;

        public FieldModificationTask(String header, Class<? extends EntityModifier> modifierClass, Method readMethod, Method writeMethod) {
            this.header = header;
            this.modifierClass = modifierClass;
            this.readMethod = readMethod;
            this.writeMethod = writeMethod;
        }
    }

    private final Map<String, FieldModificationTask> modificationTaskList = new HashMap<>();
    private final List<Class<? extends RecordModifier>> recordModificationTaskList = new ArrayList<>();

    public void initialize(Class<T> dataSampleClass, CSVModel<T> csvInput) throws IOException {
        try {
            this.csvInput = csvInput;
            // Calculate all of the field modification tasks by reading the annotations on DataSample.
            // There must be a @Parsed annotation identifying the header and
            // a @Modifier annotation identifying the modification processor
            PropertyDescriptor[] propertyDescriptors = Introspector.getBeanInfo(dataSampleClass).getPropertyDescriptors();

            Field[] declaredFields = dataSampleClass.getDeclaredFields();
            for (Field field : declaredFields) {
                final Parsed parsed = field.getAnnotation(Parsed.class);
                final Modifier modifier = field.getAnnotation(Modifier.class);
                if (parsed != null && modifier != null) {
                    for (PropertyDescriptor propertyDescriptor : propertyDescriptors) {
                        if (propertyDescriptor.getName().equals(field.getName())) {
                            modificationTaskList.put(field.getName(),
                                    new FieldModificationTask(parsed.field(), modifier.modifier(),
                                            propertyDescriptor.getReadMethod(), propertyDescriptor.getWriteMethod()
                                    )
                            );
                        }
                    }
                }
            }
        } catch (IntrospectionException e) {
            throw new IOException("Error processing modifications: " + e.getMessage());
        }
    }

    public CSVModel<T> makeModifications() throws IOException {
        for (T row : csvInput.getRecords()) {
            // Make all the modifications in modificationTaskList
            for (Map.Entry<String, FieldModificationTask> es : modificationTaskList.entrySet()) {
                FieldModificationTask modificationTask = es.getValue();
                try {
                    // Yes its an already instantiated singleton
                    EntityModifier emi = applicationContext.getBean(modificationTask.modifierClass);
                    Object in = modificationTask.readMethod.invoke(row);
                    if (in != null) {
                        Object out = emi.doModify(in);
                        modificationTask.writeMethod.invoke(row, out);
                    }
                } catch (IllegalAccessException | InvocationTargetException | BeansException e) {
                    throw new IOException("Error processing modifications: " + e.getMessage());
                }
            }
            // Make the record modifications
            for (Class<? extends RecordModifier> modifier : recordModificationTaskList) {
                RecordModifier<T> rm = applicationContext.getBean(modifier);
                rm.doProcess(row);
            }
        }
        return csvInput;
    }
}