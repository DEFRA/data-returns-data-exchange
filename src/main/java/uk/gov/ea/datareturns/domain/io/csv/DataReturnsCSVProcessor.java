/**
 *
 */
package uk.gov.ea.datareturns.domain.io.csv;

import java.io.File;
import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.univocity.parsers.annotations.Parsed;
import com.univocity.parsers.common.ParsingContext;
import com.univocity.parsers.common.TextParsingException;
import com.univocity.parsers.common.processor.BeanListProcessor;
import com.univocity.parsers.common.processor.BeanWriterProcessor;
import com.univocity.parsers.csv.CsvParser;
import com.univocity.parsers.csv.CsvParserSettings;
import com.univocity.parsers.csv.CsvWriter;
import com.univocity.parsers.csv.CsvWriterSettings;

import uk.gov.ea.datareturns.domain.io.csv.generic.CSVModel;
import uk.gov.ea.datareturns.domain.io.csv.generic.exceptions.InconsistentRowException;
import uk.gov.ea.datareturns.domain.model.MonitoringDataRecord;
import uk.gov.ea.datareturns.domain.model.rules.DataReturnsHeaders;
import uk.gov.ea.datareturns.exception.application.DRFileTypeUnsupportedException;
import uk.gov.ea.datareturns.exception.application.DRHeaderFieldUnrecognisedException;
import uk.gov.ea.datareturns.exception.application.DRHeaderMandatoryFieldMissingException;
import uk.gov.ea.datareturns.exception.application.DRInconsistentCSVException;
import uk.gov.ea.datareturns.exception.system.DRSystemException;

/**
 * Data Returns CSV reader/writer for DEP compliant CSV files.
 *
 * @author Sam Gardner-Dell
 */
public class DataReturnsCSVProcessor {
	
	private static final Map<String, String> fieldMap = new HashMap<>();
	static {
		for (final Field f : MonitoringDataRecord.class.getDeclaredFields()) {
			final Parsed annotation = f.getAnnotation(Parsed.class);
			if (annotation != null) {
				final String csvFieldName = annotation.field();
				fieldMap.put(f.getName(), csvFieldName);
			}
		}
	}
	
	public DataReturnsCSVProcessor() {
		
	}
	
	public CSVModel<MonitoringDataRecord> read(final File csvFile) {
	    BeanListProcessor<MonitoringDataRecord> rowProcessor = new BeanListProcessor<MonitoringDataRecord>(MonitoringDataRecord.class) {
			@Override
			public MonitoringDataRecord createBean(String[] row, ParsingContext context) {
				if (row.length > context.headers().length) {
					throw new InconsistentRowException(
							String.format("Record %d contains additional fields not defined in the header.",
									context.currentRecord()));
				}
				return super.createBean(row, context);
			}

			/* (non-Javadoc)
			 * @see com.univocity.parsers.common.processor.BeanListProcessor#beanProcessed(java.lang.Object, com.univocity.parsers.common.ParsingContext)
			 */
			@Override
			public void beanProcessed(MonitoringDataRecord bean, ParsingContext context) {
				bean.setLineNumber(context.currentRecord());
				super.beanProcessed(bean, context);
			}
	    };
	    CsvParserSettings parserSettings = new CsvParserSettings();
	    parserSettings.setHeaderExtractionEnabled(true);
	    parserSettings.setLineSeparatorDetectionEnabled(true);
	    parserSettings.trimValues(true);
	    parserSettings.setRowProcessor(rowProcessor);

	    // creates a parser instance with the given settings
	    CsvParser parser = new CsvParser(parserSettings);
		try {
	    	parser.parse(csvFile);
		} catch (final InconsistentRowException e) {
			// Row encountered with an inconsistent number of fields with respect to the header definitions.
			throw new DRInconsistentCSVException(e.getMessage());
		} catch (final TextParsingException e) {
			throw new DRFileTypeUnsupportedException("Unable to parse CSV file.  File content is not valid CSV data.");
		} catch (final Throwable e) {
			throw new DRSystemException(e, "Failed to parse CSV file.");
		}
    	
	    String[] headers = rowProcessor.getHeaders();
		
		// Get working sets for the list of all headers and the list of mandatory headers
		final Set<String> allHeaders = DataReturnsHeaders.getAllHeadings();
		final Set<String> mandatoryHeaders = DataReturnsHeaders.getMandatoryHeadings();
		// Set of headers defined in the supplied model (from the CSV file)
		final Set<String> csvHeaders = new LinkedHashSet<>();
		if (headers != null) {
			csvHeaders.addAll(Arrays.asList(headers));
		}
		
		
		// If we remove the CSV file's headers from the set of mandatory headers then the mandatory headers set should be empty
		// if they have defined everything that they should have.
		mandatoryHeaders.removeAll(csvHeaders);
		if (!mandatoryHeaders.isEmpty()) {
			throw new DRHeaderMandatoryFieldMissingException("Missing fields: " + mandatoryHeaders.toString());
		}

		// Create a temporary set (which we can modify) of the fields defined in the CSV file
		final Set<String> tempCsvHeaderSet = new HashSet<>(csvHeaders);
		// Remove the set of all known headers from the temporary CSV file header list.  If the resultant set is not empty, then
		// headers have been defined in the CSV file which are not allowed by the system.
		tempCsvHeaderSet.removeAll(allHeaders);
		if (!tempCsvHeaderSet.isEmpty()) {
			throw new DRHeaderFieldUnrecognisedException("Unrecognised field(s) encountered: " + tempCsvHeaderSet.toString());
		}
		
	    List<MonitoringDataRecord> records = rowProcessor.getBeans();
	    CSVModel<MonitoringDataRecord> model = new CSVModel<>();
	    model.setPojoFieldToHeaderMap(fieldMap);
	    model.setRecords(records);
	    return model;
	}
	
	public void write(List<MonitoringDataRecord> records, File csvFile) {
		CsvWriterSettings settings = new CsvWriterSettings();
		settings.setHeaders(DataReturnsHeaders.getAllHeadingsArray());

	    BeanWriterProcessor<MonitoringDataRecord> processor = new BeanWriterProcessor<MonitoringDataRecord>(MonitoringDataRecord.class);
	    settings.setRowWriterProcessor(processor);
	    CsvWriter writer = new CsvWriter(csvFile, settings);
	    // Write the record headers of this file
	    writer.writeHeaders();
	    writer.processRecordsAndClose(records);
	}
}
