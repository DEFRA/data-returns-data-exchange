/**
 *
 */
package uk.gov.ea.datareturns.domain.model.rules.conversion;

import com.univocity.parsers.conversions.Conversion;

import uk.gov.ea.datareturns.domain.model.ReturnsDate;

/**
 * Convert between a date specified as a {@link String} and a {@link ReturnsDate} model
 *
 * @author Sam Gardner-Dell
 */
public class ReturnsDateConverter implements Conversion<String, ReturnsDate> {
	/**
	 * Required for univocity parser to construct instances
	 *
	 * @param args Initialisation arguments for the converter
	 */
	public ReturnsDateConverter(final String... args) {
	}

	@Override
	public ReturnsDate execute(final String input) {
		return ReturnsDate.from(input);
	}

	@Override
	public String revert(final ReturnsDate input) {
		return input.toStandardisedFormat();
	}
}
