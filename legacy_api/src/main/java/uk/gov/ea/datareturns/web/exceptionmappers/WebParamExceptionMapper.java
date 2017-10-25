package uk.gov.ea.datareturns.web.exceptionmappers;

import org.glassfish.jersey.server.ParamException;
import org.springframework.stereotype.Component;
import uk.gov.ea.datareturns.web.resource.v1.model.response.ErrorResponse;

import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;

/**
 * Exception handler for subclasses of the {@link ParamException} family of client errors
 *
 * @author Sam Gardner-Dell
 */
@Provider
@Component
public class WebParamExceptionMapper implements ExceptionMapper<ParamException> {

    /**
     * Handle all subclasses of {@link ParamException}
     *
     * @param exception the exception which occurred
     * @return a {@link Response} to return to the client for the exception that was thrown
     */
    @Override
    public Response toResponse(final ParamException exception) {
        ErrorResponse entity = new ErrorResponse(exception.getResponse().getStatus(), exception.toString());
        return Response.fromResponse(exception.getResponse()).entity(entity).build();
    }
}