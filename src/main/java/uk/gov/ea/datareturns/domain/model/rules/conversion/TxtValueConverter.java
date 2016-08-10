/**
 *
 */
package uk.gov.ea.datareturns.domain.model.rules.conversion;

import com.univocity.parsers.conversions.Conversion;

import uk.gov.ea.datareturns.domain.model.rules.BooleanValue;

/**
 * Pre-processes Txt_Value values to ensure that the DEP allowed boolean values (see {@link BooleanValue}) are standardised
 * on the standard boolean String representations (true or false)
 *
 * @author Sam Gardner-Dell
 */
public class TxtValueConverter implements Conversion<String, String> {
	/**
	 * Required for univocity parser to construct instances
	 * @param args Initialisation arguments for the converter
	 */
	public TxtValueConverter(final String... args) {
	}

	/* (non-Javadoc)
	 * @see com.univocity.parsers.conversions.Conversion#execute(java.lang.Object)
	 */
	@Override
	public String execute(final String input) {
		final Boolean booleanValue = BooleanValue.from(input);
		if (booleanValue != null) {
			return booleanValue.toString();
		}
		return input;
	}

	/* (non-Javadoc)
	 * @see com.univocity.parsers.conversions.Conversion#revert(java.lang.Object)
	 */
	@Override
	public String revert(final String input) {
		// Map back unchanged.
		return input;
	}
}
