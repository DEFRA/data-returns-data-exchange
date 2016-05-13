package uk.gov.ea.datareturns.resource;

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
            requestContext.abortWith(Response
                    .status(Response.Status.UNAUTHORIZED)
                    .entity("Cannot access this resource")
                    .build());
        }
    }
}
