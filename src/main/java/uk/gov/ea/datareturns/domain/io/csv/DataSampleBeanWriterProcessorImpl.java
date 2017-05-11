package uk.gov.ea.datareturns.domain.io.csv;

import com.samskivert.mustache.Template;
import com.univocity.parsers.common.CommonSettings;
import com.univocity.parsers.common.processor.BeanWriterProcessor;
import com.univocity.parsers.csv.CsvWriter;
import com.univocity.parsers.csv.CsvWriterSettings;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import uk.gov.ea.datareturns.config.ProcessorSettings;
import uk.gov.ea.datareturns.domain.validation.model.DataSample;
import uk.gov.ea.datareturns.domain.validation.model.rules.FieldDefinition;
import uk.gov.ea.datareturns.domain.validation.model.rules.FieldMapping;
import uk.gov.ea.datareturns.util.MustacheTemplates;

import javax.inject.Inject;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by sam on 12/01/17.
 */
@Component
@Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class DataSampleBeanWriterProcessorImpl extends BeanWriterProcessor<DataSample> implements DataSampleBeanWriterProcessor {
    private final static Map<String, FieldMapping> MAPPINGS = FieldMapping.getFieldNameToBeanMap(DataSample.class);
    private final Map<String, String> outputTemplates;

    /**
     * Instantiates a new DataSampleBeanWriterProcessorImpl
     *
     * @param processorSettings the processor settings
     */
    @Inject
    public DataSampleBeanWriterProcessorImpl(final ProcessorSettings processorSettings) {
        super(DataSample.class);
        this.outputTemplates = processorSettings.getOutputMappingsMap();
    }

    /**
     * Configure the univocity {@link CsvWriterSettings} for this writer processor
     *
     * @param settings the univocity {@link CsvWriterSettings} to be configured
     */
    @Override public void configure(CsvWriterSettings settings) {
        settings.setHeaders(outputTemplates.keySet().toArray(new String[outputTemplates.size()]));
        settings.setRowWriterProcessor(this);
    }

    /**
     * Write a record to an output writer
     *
     * Provides an AOP join-point to allow aspects to hook the process as each record is written
     *
     * @param writer the writer to output the record to
     * @param record the record to output
     */
    @Override public void write(CsvWriter writer, DataSample record) {
        writer.processRecord(record);
    }

    /**
     * Converts the java bean instance into a sequence of values for writing.
     *
     * @param record the record to be serialized
     * @param headers All field names used to produce records in a given destination. May be null if no headers have been defined in {@link CommonSettings#getHeaders()}
     * @param indexesToWrite The indexes of the headers that are actually being written. May be null if no fields have been selected using {@link CommonSettings#selectFields(String...)} or {@link CommonSettings#selectIndexes(Integer...)}
     * @return a row of objects containing the values extracted from the java bean
     */
    @Override
    public Object[] write(final DataSample record, final String[] headers, final int[] indexesToWrite) {
        super.initialize();

        // Build a map of CSV header names to the values that should be used for the current row
        final Map<String, String> outputData = new HashMap<>();
        for (String fieldName : FieldDefinition.ALL_FIELD_NAMES_ARR) {
            outputData.put(fieldName, MAPPINGS.get(fieldName).getOutputValue(record));
        }

        // Execute all mustache templates providing the CSV header to value map we formed above.
        final String[] outputValues = new String[outputTemplates.size()];
        int index = 0;
        for (final Map.Entry<String, String> entry : outputTemplates.entrySet()) {
            final Template tmpl = MustacheTemplates.get(entry.getKey(), entry.getValue());
            outputValues[index++] = tmpl.execute(outputData);
        }
        return outputValues;
    }
}
