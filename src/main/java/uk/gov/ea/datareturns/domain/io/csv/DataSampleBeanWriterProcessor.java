package uk.gov.ea.datareturns.domain.io.csv;

import com.univocity.parsers.common.processor.RowWriterProcessor;
import com.univocity.parsers.csv.CsvWriter;
import com.univocity.parsers.csv.CsvWriterSettings;
import uk.gov.ea.datareturns.domain.model.DataSample;

/**
 * Univocity {@link RowWriterProcessor} extension for data returns
 */
public interface DataSampleBeanWriterProcessor extends RowWriterProcessor<DataSample> {
    /**
     * Configure the univocity {@link CsvWriterSettings} for this writer processor
     *
     * @param settings the univocity {@link CsvWriterSettings} to be configured
     */
    void configure(CsvWriterSettings settings);

    /**
     * Write a record to an output writer
     *
     * Provides an AOP join-point to allow aspects to hook the process as each record is written
     *
     * @param writer the writer to output the record to
     * @param record the record to output
     */
    void write(CsvWriter writer, DataSample record);
}