package uk.gov.ea.datareturns.web.exceptionmappers;

import org.glassfish.jersey.message.internal.HeaderValueException;
import uk.gov.ea.datareturns.web.resource.v1.model.response.ErrorResponse;

import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;

/**
 * Map {@link HeaderValueException}'s to a 400 response
 *
 * @author Sam Gardner-Dell
 */
@Provider
public class HeaderValueExceptionMapper implements ExceptionMapper<HeaderValueException> {
    /**
     * Handle all subclasses of {@link HeaderValueException}
     *
     * @param exception the exception which occurred
     * @return a {@link Response} to return to the client for the exception that was thrown
     */
    @Override
    public Response toResponse(final HeaderValueException exception) {
        ErrorResponse entity = new ErrorResponse(Response.Status.BAD_REQUEST, exception.toString());
        return entity.toResponseBuilder().build();
    }
}