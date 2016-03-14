/**
 * 
 */
package uk.gov.ea.datareturns.domain.io.csv;

import java.util.Map;

import uk.gov.ea.datareturns.domain.io.csv.exceptions.ValidationException;

/**
 * Interface to enable the validation of header fields during CSV parsing.
 * 
 * @author Sam Gardner-Dell
 */
public interface CSVHeaderValidator {

	/**
	 * Perform validation on the supplied header map
	 * 
	 * The supplied {@link Map} gives a mapping from the header value text (key) to its column index within the CSV (value)
	 * 
	 * @param headerMap the CSV file header map
	 * @return true if the headers are valid, false otherwise.
	 */
	/**
	 * @param headerMap
	 * @return
	 * @throws ValidationException
	 */
	void validateHeaders(Map<String, Integer> headerMap) throws ValidationException;
}
