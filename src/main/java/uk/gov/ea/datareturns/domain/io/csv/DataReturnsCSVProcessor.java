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
import com.univocity.parsers.common.processor.BeanListProcessor;
import com.univocity.parsers.common.processor.BeanWriterProcessor;
import com.univocity.parsers.conversions.Conversions;
import com.univocity.parsers.csv.CsvParser;
import com.univocity.parsers.csv.CsvParserSettings;
import com.univocity.parsers.csv.CsvWriter;
import com.univocity.parsers.csv.CsvWriterSettings;

import uk.gov.ea.datareturns.domain.exceptions.AbstractValidationException;
import uk.gov.ea.datareturns.domain.exceptions.FileStructureException;
import uk.gov.ea.datareturns.domain.exceptions.FileTypeUnsupportedException;
import uk.gov.ea.datareturns.domain.exceptions.MandatoryFieldMissingException;
import uk.gov.ea.datareturns.domain.exceptions.UnrecognisedFieldException;
import uk.gov.ea.datareturns.domain.io.csv.generic.CSVModel;
import uk.gov.ea.datareturns.domain.io.csv.generic.exceptions.InconsistentRowException;
import uk.gov.ea.datareturns.domain.model.MonitoringDataRecord;
import uk.gov.ea.datareturns.domain.model.rules.DataReturnsHeaders;

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

	public CSVModel<MonitoringDataRecord> read(final File csvFile) throws AbstractValidationException {

		final BeanListProcessor<MonitoringDataRecord> rowProcessor = new BeanListProcessor<MonitoringDataRecord>(
				MonitoringDataRecord.class) {
			@Override
			public MonitoringDataRecord createBean(final String[] row, final ParsingContext context) {
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
			public void beanProcessed(final MonitoringDataRecord bean, final ParsingContext context) {
				bean.setLineNumber(context.currentRecord() + 1);
				super.beanProcessed(bean, context);
			}
		};
		// Trim the content when converting into the bean
		rowProcessor.convertAll(Conversions.trim());

		final CsvParserSettings parserSettings = new CsvParserSettings();
		parserSettings.setHeaderExtractionEnabled(true);
		parserSettings.setLineSeparatorDetectionEnabled(true);
		parserSettings.trimValues(true);
		parserSettings.setRowProcessor(rowProcessor);

		// creates a parser instance with the given settings
		final CsvParser parser = new CsvParser(parserSettings);
		try {
			parser.parse(csvFile);
		} catch (final InconsistentRowException e) {
			// Row encountered with an inconsistent number of fields with respect to the header definitions.
			throw new FileStructureException(e.getMessage());
		} catch (final Throwable e) {
			throw new FileTypeUnsupportedException("Unable to parse CSV file.  File content is not valid CSV data.");
		}

		final String[] headers = rowProcessor.getHeaders();

		// Get working sets for the list of all headers and the list of mandatory headers
		final Set<String> allHeaders = DataReturnsHeaders.getAllHeadings();
		final Set<String> mandatoryHeaders = new HashSet<>(DataReturnsHeaders.getMandatoryHeadings());
		// Set of headers defined in the supplied model (from the CSV file)
		final Set<String> csvHeaders = new LinkedHashSet<>();
		if (headers != null) {
			csvHeaders.addAll(Arrays.asList(headers));
		}

		// If we remove the CSV file's headers from the set of mandatory headers then the mandatory headers set should be empty
		// if they have defined everything that they should have.
		mandatoryHeaders.removeAll(csvHeaders);
		if (!mandatoryHeaders.isEmpty()) {
			throw new MandatoryFieldMissingException("Missing fields: " + mandatoryHeaders.toString());
		}

		// Create a temporary set (which we can modify) of the fields defined in the CSV file
		final Set<String> tempCsvHeaderSet = new HashSet<>(csvHeaders);
		// Remove the set of all known headers from the temporary CSV file header list.  If the resultant set is not empty, then
		// headers have been defined in the CSV file which are not allowed by the system.
		tempCsvHeaderSet.removeAll(allHeaders);
		if (!tempCsvHeaderSet.isEmpty()) {
			throw new UnrecognisedFieldException("Unrecognised field(s) encountered: " + tempCsvHeaderSet.toString());
		}

		final List<MonitoringDataRecord> records = rowProcessor.getBeans();
		final CSVModel<MonitoringDataRecord> model = new CSVModel<>();
		model.setPojoFieldToHeaderMap(fieldMap);
		model.setRecords(records);
		return model;
	}

	public void write(final List<MonitoringDataRecord> records, final File csvFile) {
		final CsvWriterSettings settings = new CsvWriterSettings();
		settings.setHeaders(DataReturnsHeaders.getAllHeadingsArray());

		final BeanWriterProcessor<MonitoringDataRecord> processor = new BeanWriterProcessor<MonitoringDataRecord>(
				MonitoringDataRecord.class);
		settings.setRowWriterProcessor(processor);
		final CsvWriter writer = new CsvWriter(csvFile, settings);
		// Write the record headers of this file
		writer.writeHeaders();
		writer.processRecordsAndClose(records);
	}
}
