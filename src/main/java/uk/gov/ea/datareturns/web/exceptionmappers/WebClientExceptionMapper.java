package uk.gov.ea.datareturns.web.exceptionmappers;

import org.springframework.stereotype.Component;
import uk.gov.ea.datareturns.web.resource.v1.model.response.ErrorResponse;

import javax.ws.rs.ClientErrorException;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;

/**
 * Exception handler for subclasses of the {@link javax.ws.rs.ClientErrorException}
 *
 * @author Sam Gardner-Dell
 */
@Provider
@Component
public class WebClientExceptionMapper implements ExceptionMapper<ClientErrorException> {
    /**
     * Handle all subclasses of {@link ClientErrorException}
     *
     * @param exception the exception which occurred
     * @return a {@link Response} to return to the client for the exception that was thrown
     */
    @Override
    public Response toResponse(final ClientErrorException exception) {
        ErrorResponse entity = new ErrorResponse(exception.getResponse().getStatus(), exception.toString());
        return Response.fromResponse(exception.getResponse()).entity(entity).build();
    }
}