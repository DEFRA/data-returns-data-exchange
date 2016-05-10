package uk.gov.ea.datareturns.domain.model.rules;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

/**
 * Provides a mapping of allowed boolean text values to a Boolean object 
 * 
 * @author Sam Gardner-Dell
 */
public final class BooleanValue {
	private static final Map<String, Boolean> mapping = new HashMap<>();
	static {
		// All values should be lowercase as we use case-insensitive matching
		mapping.put("true", Boolean.TRUE);
		mapping.put("yes", Boolean.TRUE);
		mapping.put("1", Boolean.TRUE);

		mapping.put("false", Boolean.FALSE);
		mapping.put("no", Boolean.FALSE);
		mapping.put("0", Boolean.FALSE);
	}

	private BooleanValue() {}

	/**
	 * Returns a {@link Boolean} value from the given {@link Object} based on the rules defined in the DEP
	 * 
	 * This method uses case-insensitive matching to 
	 * 
	 * @param o the {@link Object} to convert to a boolean
	 * @return a {@link Boolean} if a mapping exists, null otherwise
	 */
	public static Boolean from(Object o) {
		return mapping.get(Objects.toString(o).toLowerCase());
	}
}