/**
 *
 */
package uk.gov.ea.datareturns.email;

/**
 * @author Sam Gardner-Dell
 *
 */
public class MonitorProTransportException extends Exception {
	/** Appease the gods of serialization */
	private static final long serialVersionUID = 1L;

	public MonitorProTransportException(final String message) {
		super(message);
	}

	public MonitorProTransportException(final String message, final Throwable cause) {
		super(message, cause);
	}
}
