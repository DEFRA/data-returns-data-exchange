package uk.gov.ea.datareturns.domain.io.csv.generic.exceptions;

public class ValidationException extends Exception {
	/** Appease the gods of serialization */
	private static final long serialVersionUID = 1L;

	public ValidationException(final String message) {
		super(message);
	}
}
