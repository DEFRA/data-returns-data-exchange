package uk.gov.ea.datareturns.domain.io.csv;

import uk.gov.ea.datareturns.domain.exceptions.AbstractValidationException;
import uk.gov.ea.datareturns.domain.model.DataSample;

import java.io.File;
import java.io.InputStream;
import java.util.List;

/**
 * Data Returns CSV reader/writer for DEP compliant CSV files.
 *
 * @author Sam Gardner-Dell
 */
public interface DataReturnsCSVProcessor {
    /**
     * Read the content of the specified DEP compliant CSV file into the Java model
     *
     * @param inputStream an {@link InputStream} from which DEP compliant CSV data can be read
     * @return a {@link List} composed of {@link DataSample} objects to represent the samples/readings submitted
     * @throws AbstractValidationException if a validation error occurs when attempting to read the DEP compliant CSV
     */
    List<DataSample> read(InputStream inputStream) throws AbstractValidationException;

    /**
     * Writes a CSV file based on the mappings specified in the configuration file.
     *
     * @param records the data returns records to be written
     * @param csvFile a reference to the {@link File} to be written
     */
    void write(List<DataSample> records, File csvFile);
}
