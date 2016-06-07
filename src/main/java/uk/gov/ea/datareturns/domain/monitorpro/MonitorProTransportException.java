/**
 *
 */
package uk.gov.ea.datareturns.domain.monitorpro;

import uk.gov.ea.datareturns.domain.exceptions.ProcessingException;

/**
 * Exception type for problems sending data to MonitorPro
 *
 * @author Sam Gardner-Dell
 */
public class MonitorProTransportException extends ProcessingException {
	/** Appease the gods of serialization */
	private static final long serialVersionUID = 1L;

	/**
	 * Create a new MonitorProTransportException
	 *
	 * @param message the detailed exception message
	 */
	public MonitorProTransportException(final String message) {
		super(message);
	}

	/**
	 * Create a new MonitorProTransportException
	 *
	 * @param message the detailed exception message
	 * @param cause the underlying cause of the exception
	 */
	public MonitorProTransportException(final String message, final Throwable cause) {
		super(message, cause);
	}
}
