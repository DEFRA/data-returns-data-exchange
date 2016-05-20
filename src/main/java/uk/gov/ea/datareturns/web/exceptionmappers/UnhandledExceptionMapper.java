/**
 *
 */
package uk.gov.ea.datareturns.web.exceptionmappers;

import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import uk.gov.ea.datareturns.domain.exceptions.ApplicationExceptionType;
import uk.gov.ea.datareturns.domain.result.ExceptionMessageContainer;

/**
 * Catch-all Exception mapper
 *
 * @author Sam Gardner-Dell
 */
@Provider
public class UnhandledExceptionMapper implements ExceptionMapper<Throwable> {
	private static final Logger LOGGER = LoggerFactory.getLogger(UnhandledExceptionMapper.class);

	/* (non-Javadoc)
	 * @see javax.ws.rs.ext.ExceptionMapper#toResponse(java.lang.Throwable)
	 */
	@Override
	public Response toResponse(final Throwable exception) {
		LOGGER.error("He's Dead Jim", exception);
		final Status status = Status.INTERNAL_SERVER_ERROR;
		final ExceptionMessageContainer entity = new ExceptionMessageContainer(ApplicationExceptionType.SYSTEM_FAILURE,
				exception.toString());
		return Response.status(status).entity(entity).type(MediaType.APPLICATION_JSON_TYPE).build();
	}
}