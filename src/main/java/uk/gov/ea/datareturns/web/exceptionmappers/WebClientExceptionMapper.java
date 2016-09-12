package uk.gov.ea.datareturns.web.exceptionmappers;

import uk.gov.ea.datareturns.domain.exceptions.ApplicationExceptionType;
import uk.gov.ea.datareturns.domain.result.ExceptionMessageContainer;

import javax.ws.rs.ClientErrorException;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;

/**
 * Exception handler for subclasses of the {@link javax.ws.rs.ClientErrorException}
 *
 * @author Sam Gardner-Dell
 */
@Provider
public class WebClientExceptionMapper implements ExceptionMapper<ClientErrorException> {
    /**
     * Handle all subclasses of {@link ClientErrorException}
     *
     * @param exception the exception which occurred
     * @return a {@link Response} to return to the client for the exception that was thrown
     */
    @Override
    public Response toResponse(final ClientErrorException exception) {
        final ExceptionMessageContainer entity = new ExceptionMessageContainer(ApplicationExceptionType.CLIENT_FAILURE, exception.toString());
        return Response.fromResponse(exception.getResponse()).entity(entity).type(MediaType.APPLICATION_JSON_TYPE).build();
    }
}