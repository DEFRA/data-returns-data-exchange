package uk.gov.ea.datareturns.domain.processors;

import com.univocity.parsers.annotations.Parsed;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Component;
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
@Component
public class ModificationProcessor implements ApplicationContextAware {
    private static final Logger LOGGER = LoggerFactory.getLogger(ModificationProcessor.class);
    private ApplicationContext applicationContext;

    private CSVModel<DataSample> csvInput;

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
    }

    private class ModificationTask {
        protected Class<? extends EntityModifier> modifierClass;
        protected String header;
        protected Method readMethod;
        protected Method writeMethod;

        public ModificationTask(String header, Class<? extends EntityModifier> modifierClass, Method readMethod, Method writeMethod) throws IllegalAccessException, InstantiationException {
            this.header = header;
            this.modifierClass = modifierClass;
            this.readMethod = readMethod;
            this.writeMethod = writeMethod;
        }
    }

    public void initialize(CSVModel<DataSample> csvInput) throws IOException {
        try {
            this.csvInput = csvInput;
            PropertyDescriptor[] propertyDescriptors = null;

            // Calculate all of the modification tasks by reading the annotations on DataSample.
            // There must be a @Parsed annotation identifying the header and
            // a @Modifier annotation identifying the modification processor
            propertyDescriptors = Introspector.getBeanInfo(DataSample.class).getPropertyDescriptors();

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
        } catch (IntrospectionException|IllegalAccessException|InstantiationException e) {
            throw new IOException("Error processing modifications: " + e.getMessage());
        }
    }

    private Map<String, ModificationTask> modificationTaskList  = new HashMap<>();

    public CSVModel<DataSample> performSubstitutions() throws IOException {
        for (DataSample row : csvInput.getRecords()) {
            // Make all the modifications in modificationTaskList
            for (Map.Entry<String, ModificationTask> es : modificationTaskList.entrySet()) {
                ModificationTask modificationTask = es.getValue();
                try {
                    EntityModifier emi = applicationContext.getBean(modificationTask.modifierClass);
                    Object in = modificationTask.readMethod.invoke(row);
                    if (in != null) {
                        Object out = emi.doModify(in);
                        modificationTask.writeMethod.invoke(row, out);
                    }
                } catch (IllegalAccessException|InvocationTargetException|BeansException e) {
                    throw new IOException("Error processing modifications: " + e.getMessage());
                }
            }
        }
        return csvInput;
    }
}
