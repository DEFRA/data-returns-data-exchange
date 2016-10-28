package uk.gov.ea.datareturns.web.exceptionmappers;

import org.glassfish.jersey.server.ParamException;
import uk.gov.ea.datareturns.domain.exceptions.ApplicationExceptionType;
import uk.gov.ea.datareturns.domain.result.ExceptionMessageContainer;

import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;

/**
 * Exception handler for subclasses of the {@link ParamException} family of client errors
 *
 * @author Sam Gardner-Dell
 */
@Provider
public class WebParamExceptionMapper implements ExceptionMapper<ParamException> {

    /**
     * Handle all subclasses of {@link ParamException}
     *
     * @param exception the exception which occurred
     * @return a {@link Response} to return to the client for the exception that was thrown
     */
    @Override
    public Response toResponse(final ParamException exception) {
        final ExceptionMessageContainer entity = new ExceptionMessageContainer(ApplicationExceptionType.CLIENT_FAILURE, exception.toString());
        return Response.fromResponse(exception.getResponse()).entity(entity).type(MediaType.APPLICATION_JSON_TYPE).build();
    }
}