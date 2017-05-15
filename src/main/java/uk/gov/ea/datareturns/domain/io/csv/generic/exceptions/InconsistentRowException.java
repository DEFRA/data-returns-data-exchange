package uk.gov.ea.datareturns.domain.io.csv.generic.exceptions;

import com.univocity.parsers.common.DataProcessingException;

/**
 * Thrown by the reader if a row is encountered with an inconsistent number of entityfields with respect to the header.
 *
 * @author Sam Gardner-Dell
 */
public class InconsistentRowException extends DataProcessingException {
    /** Appease the gods of serialization */
    private static final long serialVersionUID = 1L;

    /**
     * Create a new {@link InconsistentRowException}
     *
     * @param message the detailed exception message
     */
    public InconsistentRowException(final String message) {
        super(message);
    }
}
