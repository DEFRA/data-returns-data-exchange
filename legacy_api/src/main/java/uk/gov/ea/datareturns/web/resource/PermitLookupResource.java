package uk.gov.ea.datareturns.web.resource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import uk.gov.ea.datareturns.domain.dto.impl.PermitLookupDto;
import uk.gov.ea.datareturns.domain.processors.SearchProcessor;

import javax.inject.Inject;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Response;

import static javax.ws.rs.core.MediaType.APPLICATION_JSON;

/**
 * @Author Graham Willis
 * Endpoints for permit lookup RESTful service
 */
@Path("/lookup/")
@Component
@Scope(ConfigurableBeanFactory.SCOPE_SINGLETON)
public class PermitLookupResource implements JerseyResource {

    private static final Logger LOGGER = LoggerFactory.getLogger(PermitLookupResource.class);
    private final SearchProcessor searchProcessor;

    @Inject
    public PermitLookupResource(SearchProcessor searchProcessor) {
        this.searchProcessor = searchProcessor;
    }

    @GET
    @Path("/permit")
    @Produces(APPLICATION_JSON)
    public Response searchBySiteOrPermit(@QueryParam("term") final String term) throws Exception {
        LOGGER.debug("Request for /search/site-permit/");
        LOGGER.debug("Requested search term: " + term == null ? "" : term);
        if (term == null || term.isEmpty()) {
            LOGGER.error("Site/Permit lookup requires search term");
            return Response.status(Response.Status.BAD_REQUEST).build();
        } else {
            PermitLookupDto dto = searchProcessor.getBySiteOrPermit(term);
            return Response.status(Response.Status.OK).entity(dto).build();
        }
    }
}
