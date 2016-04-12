/**
 *
 */
package uk.gov.ea.datareturns.domain.io.csv;

import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.io.FileUtils;

import uk.gov.ea.datareturns.config.DataExchangeConfiguration;
import uk.gov.ea.datareturns.config.MiscSettings;
import uk.gov.ea.datareturns.domain.io.csv.generic.CSVHeaderValidator;
import uk.gov.ea.datareturns.domain.io.csv.generic.CSVModel;
import uk.gov.ea.datareturns.domain.io.csv.generic.CSVReader;
import uk.gov.ea.datareturns.domain.io.csv.generic.CSVWriter;
import uk.gov.ea.datareturns.domain.io.csv.generic.exceptions.HeaderFieldMissingException;
import uk.gov.ea.datareturns.domain.io.csv.generic.exceptions.HeaderFieldUnrecognisedException;
import uk.gov.ea.datareturns.domain.io.csv.generic.exceptions.InconsistentRowException;
import uk.gov.ea.datareturns.domain.io.csv.generic.exceptions.ValidationException;
import uk.gov.ea.datareturns.domain.io.csv.generic.settings.CSVReaderSettings;
import uk.gov.ea.datareturns.domain.io.csv.generic.settings.CSVWriterSettings;
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

	private final DataExchangeConfiguration config;

	/**
	 *
	 */
	public DataReturnsCSVProcessor(final DataExchangeConfiguration config) {
		this.config = config;
	}

	/**
	 * Parse the given file as a CSV and return a Java model with the result.
	 *
	 * @param file
	 * @return
	 */
	public final CSVModel<MonitoringDataRecord> readCSVFile(final File csvFile) {
		final MiscSettings settings = this.config.getMiscSettings();
		final Character csvDelimiter = settings.getCSVSeparatorCharacter();

		// Create a validator for the CSV headings
		final CSVHeaderValidator validator = new CSVHeaderValidator() {
			@Override
			public void validateHeaders(final Map<String, Integer> headerMap) throws ValidationException {
				// Get working sets for the list of all headers and the list of mandatory headers
				final Set<String> allHeaders = DataReturnsHeaders.getAllHeadings();
				final Set<String> mandatoryHeaders = DataReturnsHeaders.getMandatoryHeadings();
				// Set of headers defined in the supplied model (from the CSV file)
				final Set<String> csvHeaders = headerMap.keySet();
				
				if (csvHeaders.contains(null) || csvHeaders.contains("")) {
					throw new InconsistentRowException("One or more rows contain additional fields not defined in the headers.");
				}

				// If we remove the CSV file's headers from the set of mandatory headers then the mandatory headers set should be empty
				// if they have defined everything that they should have.
				mandatoryHeaders.removeAll(csvHeaders);
				if (!mandatoryHeaders.isEmpty()) {
					throw new HeaderFieldMissingException("Missing fields: " + mandatoryHeaders.toString());
				}

				// Create a temporary set (which we can modify) of the fields defined in the CSV file
				final Set<String> tempCsvHeaderSet = new HashSet<>(csvHeaders);
				// Remove the set of all known headers from the temporary CSV file header list.  If the resultant set is not empty, then
				// headers have been defined in the CSV file which are not allowed by the system.
				tempCsvHeaderSet.removeAll(allHeaders);
				if (!tempCsvHeaderSet.isEmpty()) {
					throw new HeaderFieldUnrecognisedException("Unrecognised field(s) encountered: " + tempCsvHeaderSet.toString());
				}
			}
		};
		// Configure a CSV reader with our settings and validator - map the CSV to the MonitoringDataRecord class
		final CSVReaderSettings csvReaderSettings = new CSVReaderSettings(csvDelimiter, validator);
		csvReaderSettings.setTrimWhitespace(true);
		final CSVReader<MonitoringDataRecord> csvReader = new CSVReader<>(MonitoringDataRecord.class, csvReaderSettings);

		try {
			return csvReader.parseCSV(csvFile);
		} catch (final HeaderFieldMissingException e) {
			// CSV failed to parse due to a missing header field
			throw new DRHeaderMandatoryFieldMissingException(e.getMessage());
		} catch (final HeaderFieldUnrecognisedException e) {
			// CSV failed to parse due to an unexpected header field
			throw new DRHeaderFieldUnrecognisedException(e.getMessage());
		} catch (final InconsistentRowException e) {
			// Row encountered with an inconsistent number of fields with respect to the header definitions.
			throw new DRInconsistentCSVException(e.getMessage());
		} catch (final ValidationException e) {
			throw new DRFileTypeUnsupportedException("Unable to parse CSV file.  File content is not valid CSV data.");
		} catch (final IOException e) {
			throw new DRSystemException(e, "Failed to parse CSV file.");
		}
	}

	/**
	 * Write a list of {@link MonitoringDataRecord} entries to a {@link File}
	 *
	 * @param records the {@link List} of {@link MonitoringDataRecord} to be written
	 * @param outputFile the output {@link File} to be written to
	 */
	public final void writeCSVFile(final List<MonitoringDataRecord> records, final File outputFile) {
		final MiscSettings settings = this.config.getMiscSettings();
		final Character csvDelimiter = settings.getCSVSeparatorCharacter();
		final Set<String> allHeadings = DataReturnsHeaders.getAllHeadings();
		final CSVWriterSettings csvWriterSettings = new CSVWriterSettings(csvDelimiter, new ArrayList<>(allHeadings));
		csvWriterSettings.setTrimWhitespace(true);
		final CSVWriter<MonitoringDataRecord> writer = new CSVWriter<>(MonitoringDataRecord.class, csvWriterSettings);

		try {
			// Output to a file in outputdir/validated with the same name as the uploaded file.
			try (final OutputStream fos = FileUtils.openOutputStream(outputFile)) {
				writer.write(records, fos);
			}
		} catch (final IOException e) {
			throw new DRSystemException(e, "Unable to write validated CSV file");
		}
	}
}
