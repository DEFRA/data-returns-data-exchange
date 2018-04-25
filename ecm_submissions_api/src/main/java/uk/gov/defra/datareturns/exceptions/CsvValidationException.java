package uk.gov.defra.datareturns.exceptions;

import lombok.Getter;

/**
 * Exception type to signify when a validation error occurred when validating the model generated from the input
 * file.
 *
 * @author Sam Gardner-Dell
 */
@Getter
public class CsvValidationException extends ProcessingException {
    /**
     * Appease the gods of serialization
     */
    private static final long serialVersionUID = 1L;

    private final ApplicationExceptionType type;
    private final Integer lineNo;

    /**
     * Construct an implementation of the {@link CsvValidationException}
     *
     * @param message the detailed exception message
     */
    public CsvValidationException(final ApplicationExceptionType type, final Integer lineNo, final String message) {
        super(message);
        this.type = type;
        this.lineNo = lineNo;
    }
}
