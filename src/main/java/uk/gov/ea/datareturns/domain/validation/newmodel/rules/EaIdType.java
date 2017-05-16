package uk.gov.ea.datareturns.domain.validation.newmodel.rules;

import org.apache.commons.lang3.StringUtils;

/**
 * Enumeration to represent the type of EA Unique Identifier.
 *
 * @author Sam Gardner-Dell
 */
public enum EaIdType {
    /** EA Unique Identifiers using lowercase numeric identifier */
    LOWER_NUMERIC,
    /** EA Unique Identifiers using uppercase numeric identifier */
    UPPER_NUMERIC,
    /** EA Unique Identifiers using lowercase alphanumeric identifier */
    LOWER_ALPHANUMERIC,
    /** EA Unique Identifiers using uppercase alphanumeric identifier */
    UPPER_ALPHANUMERIC;

    /** The boundary for numeric EA Unique identifiers */
    public static final char ALPHA_NUMERIC_BOUNDARY = 'H';

    /** The boundary for alphanumeric EA Unique identifiers */
    public static final int NUMERIC_BOUNDARY = 70000;

    /**
     * Retrieve the appropriate {@link EaIdType} for a given unique identifier
     *
     * @param uniqueId the String representation of a unique identifier to test
     * @return the appropriate {@link EaIdType} for the given identifier.
     */
    public static EaIdType forUniqueId(final String uniqueId) {
        EaIdType db = null;
        if (StringUtils.isNotEmpty(uniqueId)) {
            try {
                final int numericPermit = Integer.parseInt(uniqueId);
                db = numericPermit < NUMERIC_BOUNDARY ? LOWER_NUMERIC : UPPER_NUMERIC;
            } catch (final NumberFormatException e) {
                final char startLetter = Character.toUpperCase(uniqueId.charAt(0));
                db = startLetter < ALPHA_NUMERIC_BOUNDARY ? LOWER_ALPHANUMERIC : UPPER_ALPHANUMERIC;
            }
        }
        return db;
    }
}
