/**
 *
 */
package uk.gov.ea.datareturns.domain.monitorpro;

import uk.gov.ea.datareturns.domain.exceptions.ProcessingException;

/**
 * @author Sam Gardner-Dell
 *
 */
public class MonitorProTransportException extends ProcessingException {
	/** Appease the gods of serialization */
	private static final long serialVersionUID = 1L;

	public MonitorProTransportException(final String message) {
		super(message);
	}

	public MonitorProTransportException(final String message, final Throwable cause) {
		super(message, cause);
	}
}
