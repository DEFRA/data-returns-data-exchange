/**
 *
 */
package uk.gov.ea.datareturns.domain.model.rules.conversion;

import com.univocity.parsers.conversions.Conversion;

import uk.gov.ea.datareturns.domain.model.EaId;

/**
 * Convert between a {@link String} based identifier and the {@link EaId} model
 *
 * @author Sam Gardner-Dell
 */
public class EaIdConverter implements Conversion<String, EaId> {
	/**
	 * Required for univocity parser to construct instances
	 *
	 * @param args Initialisation arguments for the converter
	 */
	public EaIdConverter(final String... args) {
	}

	/* (non-Javadoc)
	 * @see com.univocity.parsers.conversions.Conversion#execute(java.lang.Object)
	 */
	@Override
	public EaId execute(final String identifier) {
		return new EaId(identifier);
	}

	/* (non-Javadoc)
	 * @see com.univocity.parsers.conversions.Conversion#revert(java.lang.Object)
	 */
	@Override
	public String revert(final EaId identifier) {
		return identifier.getIdentifier();
	}
}
