package uk.gov.ea.datareturns.resource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import uk.gov.ea.datareturns.service.ApiKeys;

import javax.inject.Inject;
import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.container.ContainerRequestFilter;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.Response;
import java.io.IOException;

/**
 * Created by graham on 13/05/16.
 */
@FilenameAuthorization
public class AuthorizationFilterFileUpload  implements ContainerRequestFilter {
    private static final Logger LOGGER = LoggerFactory.getLogger(AuthorizationFilterFileUpload.class);

    @Inject
    ApiKeys apiKeys;

    @Override
    public void filter(ContainerRequestContext requestContext)
            throws IOException {

        String authorizationHeader =
                requestContext.getHeaderString(HttpHeaders.AUTHORIZATION);

        String filename =
                requestContext.getHeaderString("filename");

        if (!apiKeys.verifyAuthorizationHeader(authorizationHeader, filename)) {
            LOGGER.info("Unable to verify request signature: request unauthorized: " +
                    requestContext.getUriInfo().getRequestUri());

            requestContext.abortWith(Response
                    .status(Response.Status.UNAUTHORIZED)
                    .entity("Cannot access this resource")
                    .build());
        }
    }
}
