package uk.gov.ea.datareturns.web.resource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Response;
import java.util.Date;

import static javax.ws.rs.core.MediaType.APPLICATION_JSON;

/**
 * The {@link LoggingTestResource} RESTful service to server controlled list definitions
 *
 * @author Sam Gardner-Dell
 */
@Path("/logging/")
@Component
@Scope(ConfigurableBeanFactory.SCOPE_SINGLETON)
public class LoggingTestResource {
    private static final Logger LOGGER = LoggerFactory.getLogger(LoggingTestResource.class);
    /**
     * Create a new {@link LoggingTestResource} RESTful service
     */
    public LoggingTestResource() {
    }

    @GET
    @Path("/test")
    @Produces(APPLICATION_JSON)
    public Response testLogging() {
        final String timestamp = new Date().toString();
        LOGGER.debug("/logging/test endpoint invoked at " + timestamp);
        LOGGER.info("/logging/test endpoint invoked at " + timestamp);
        LOGGER.warn("/logging/test endpoint invoked at " + timestamp);
        LOGGER.error("/logging/test endpoint invoked at " + timestamp);
        return Response.status(Response.Status.OK).entity(timestamp).build();
    }
}