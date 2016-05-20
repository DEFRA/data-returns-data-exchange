package uk.gov.ea.datareturns.web.filters;

import java.io.IOException;

import javax.inject.Inject;
import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.container.ContainerRequestFilter;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.Response;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import uk.gov.ea.datareturns.web.security.ApiKeys;

/**
 * Created by graham on 13/05/16.
 */
@FilenameAuthorization
@Component
@Scope(ConfigurableBeanFactory.SCOPE_SINGLETON)
public class AuthorizationFilterFileUpload implements ContainerRequestFilter {
	private static final Logger LOGGER = LoggerFactory.getLogger(AuthorizationFilterFileUpload.class);

	private final ApiKeys apiKeys;

	@Inject
	public AuthorizationFilterFileUpload(final ApiKeys apiKeys) {
		this.apiKeys = apiKeys;
	}

	@Override
	public void filter(final ContainerRequestContext requestContext) throws IOException {
		final String authorizationHeader = requestContext.getHeaderString(HttpHeaders.AUTHORIZATION);
		final String filename = StringUtils.defaultString(requestContext.getHeaderString("filename"), "");

		if (!this.apiKeys.verifyAuthorizationHeader(authorizationHeader, filename)) {
			LOGGER.info("Unable to verify request signature: request unauthorized: " +
					requestContext.getUriInfo().getRequestUri());

			requestContext.abortWith(Response
					.status(Response.Status.UNAUTHORIZED)
					.entity("Cannot access this resource")
					.build());
		}
	}
}
