package uk.gov.ea.datareturns.web.resource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import uk.gov.ea.datareturns.domain.dto.ControlledListsDto;
import uk.gov.ea.datareturns.domain.exceptions.ApplicationExceptionType;
import uk.gov.ea.datareturns.domain.jpa.entities.ControlledListEntity;
import uk.gov.ea.datareturns.domain.jpa.entities.ControlledListsList;
import uk.gov.ea.datareturns.domain.processors.ControlledListProcessor;
import uk.gov.ea.datareturns.domain.result.ExceptionMessageContainer;

import javax.inject.Inject;
import javax.ws.rs.*;
import javax.ws.rs.core.Response;
import java.util.List;
import java.util.Map;

import static javax.ws.rs.core.MediaType.APPLICATION_JSON;

/**
 * The {@link ControlledListResource} RESTful service to server controlled list definitions
 *
 * @author Sam Gardner-Dell
 */
@Component
@Scope(ConfigurableBeanFactory.SCOPE_SINGLETON)
public class ControlledListResource {
    private static final Logger LOGGER = LoggerFactory.getLogger(ControlledListResource.class);
    /** controlled list processor */
    private final ControlledListProcessor controlledListProcessor;

    /**
     * Create a new {@link ControlledListResource} RESTful service
     *
     * @param controlledListProcessor the controlled list processor
     */
    @Inject
    public ControlledListResource(final ControlledListProcessor controlledListProcessor) {
        this.controlledListProcessor = controlledListProcessor;
    }

    @GET
    @Path("/controlled-list/")
    @Produces(APPLICATION_JSON)
    public Response listControlledLists() {
        LOGGER.debug("Request for /controlled-list");
        Map<String, ControlledListsDto> listData = controlledListProcessor.getListData();
        return Response.status(Response.Status.OK).entity(listData).build();
    }


    @GET
    @Path("/controlled-list/{listname}/")
    @Produces(APPLICATION_JSON)
    public Response getControlledList(
            @PathParam("listname") final String listName,
            @QueryParam("field") final String field,
            @QueryParam("contains") final String contains) throws Exception {
        LOGGER.debug("Request for /controlled-list/" + listName + " Field: " + field + " contains: " + contains);
        ControlledListsList controlledList = ControlledListsList.getByPath(listName);

        // Check we have a registered controlled list type
        if (controlledList == null) {
            LOGGER.error("Request for unknown controlled list: " + listName);
            return Response.status(Response.Status.BAD_REQUEST).entity(new ExceptionMessageContainer(
                    ApplicationExceptionType.UNKNOWN_LIST_TYPE, "Request for unknown controlled list: " + listName
            )).build();
        } else {
            List<? extends ControlledListEntity> listData = controlledListProcessor.getListData(controlledList, field, contains);
            return Response.status(Response.Status.OK).entity(listData).build();
        }
    }
}
