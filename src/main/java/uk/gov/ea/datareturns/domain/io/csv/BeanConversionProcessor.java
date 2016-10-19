package uk.gov.ea.datareturns.domain.io.csv;

import com.univocity.parsers.common.CommonSettings;
import com.univocity.parsers.common.ParsingContext;
import com.univocity.parsers.common.processor.RowProcessor;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import uk.gov.ea.datareturns.domain.io.csv.generic.exceptions.InconsistentRowException;
import uk.gov.ea.datareturns.domain.model.fields.MappedField;
import uk.gov.ea.datareturns.domain.model.rules.FieldMapping;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

/**
 * Data-returns specific implementation of the univocity {@link RowProcessor} which allows mapping between CSV field headings and Javabean
 * properties using the {@link MappedField} annotation.
 *
 * @param <T>  the type of bean which this processor will map to
 *
 * @author Sam Gardner-Dell
 */
public class BeanConversionProcessor<T> implements RowProcessor {
    /** Class logger */
    private static final Logger LOGGER = LoggerFactory.getLogger(BeanConversionProcessor.class);
    /** Class of the bean being mapped by this processor */
    private Class<T> beanClass;
    /** Class of the bean being mapped by this processor */
    private Map<String, FieldMapping> fieldMappings;
    /** The list of beans created by this processor when a file is processed */
    private List<T> beans;
    /** The headers read in the first row of the file */
    private String[] headers;

    /**
     * Instantiates a new Bean conversion processor.
     *
     * @param beanClass the bean class
     */
    public BeanConversionProcessor(Class<T> beanClass) {
        this.beanClass = beanClass;
        this.fieldMappings = FieldMapping.getFieldNameToBeanMap(beanClass);
    }
    /**
     * Converts a parsed row to a java object
     */
    @Override
    public final void rowProcessed(String[] row, ParsingContext context) {
        T instance = createBean(row, context);
        if (instance != null) {
            beanProcessed(instance);
        }
    }

    /**
     * Create an instance of the bean (with parameterized type T) and set the data from the current row into the appropriate fields
     *
     * @param row a String array with the data from the current processor row.
     * @param context the context the {@link ParsingContext} of the operation
     * @return the Javabean instance populated with the row of data.
     */
    public T createBean(final String[] row, final ParsingContext context) {
        T instance = null;
        try {
            // Check for inconsistent number of fields in a row with respect to the defined headers
            if (row.length != context.headers().length) {
                throw new InconsistentRowException(
                        String.format("Record %d contains %d entries but the header has %d.",
                                context.currentRecord(), row.length, context.headers().length));
            }
            instance = beanClass.newInstance();

            // Process each CSV field, mapping the data to the correct Javabean field
            for (int i = 0; i < context.headers().length; i++) {
                String header = context.headers()[i];
                String value = row[i];
                FieldMapping mapping = fieldMappings.get(header);
                if (mapping != null) {
                    if (mapping.getMappedField().trim()) {
                        value = StringUtils.trim(value);
                    }
                    mapping.setValue(instance, value);
                }
            }
        } catch (ReflectiveOperationException e) {
            LOGGER.error("Unable to instantiate bean class", e);
        }
        return instance;
    }

    /**
     * Bean processed.
     *
     * @param bean the bean
     */
    public void beanProcessed(final T bean) {
        beans.add(bean);
    }

    @Override
    public void processStarted(ParsingContext context) {
        beans = new ArrayList<>();
    }

    @Override
    public void processEnded(ParsingContext context) {
        headers = context.headers();
    }

    /**
     * Returns the record headers. This can be either the headers defined in {@link CommonSettings#getHeaders()}
     * or the headers parsed in the file when {@link CommonSettings#getHeaders()}  equals true
     *
     * @return the headers of all records parsed.
     */
    public String[] getHeaders() {
        return headers;
    }

    /**
     * Returns the list of generated java beans at the end of the parsing process.
     * @return the list of generated java beans at the end of the parsing process.
     */
    public List<T> getBeans() {
        return beans == null ? Collections.emptyList() : beans;
    }
}