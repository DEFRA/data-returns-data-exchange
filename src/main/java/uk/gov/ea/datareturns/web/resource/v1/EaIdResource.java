package uk.gov.ea.datareturns.web.resource.v1;

import io.swagger.annotations.*;
import org.springframework.stereotype.Component;
import uk.gov.ea.datareturns.domain.jpa.entities.userdata.impl.DatasetEntity;
import uk.gov.ea.datareturns.domain.jpa.entities.userdata.impl.User;
import uk.gov.ea.datareturns.web.resource.v1.model.common.Linker;
import uk.gov.ea.datareturns.web.resource.v1.model.common.Preconditions;
import uk.gov.ea.datareturns.web.resource.v1.model.common.references.EntityReference;
import uk.gov.ea.datareturns.web.resource.v1.model.dataset.Dataset;
import uk.gov.ea.datareturns.web.resource.v1.model.eaid.EaId;
import uk.gov.ea.datareturns.web.resource.v1.model.response.DatasetEntityResponse;
import uk.gov.ea.datareturns.web.resource.v1.model.response.EaIdEntityResponse;
import uk.gov.ea.datareturns.web.resource.v1.model.response.EntityReferenceListResponse;
import uk.gov.ea.datareturns.web.resource.v1.model.response.ErrorResponse;

import javax.validation.constraints.Pattern;
import javax.ws.rs.*;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;

import static javax.ws.rs.core.MediaType.APPLICATION_JSON;
import static javax.ws.rs.core.MediaType.APPLICATION_XML;
import static uk.gov.ea.datareturns.web.resource.v1.model.common.PreconditionChecks.onPreconditionsPass;

/**
 * @author Graham Willis
 * RESTful end point for EA_ID entities
 */
@Api(description = "EA_ID Resource",
        tags = { "EA ID's" },
        consumes = APPLICATION_JSON + "," + APPLICATION_XML,
        produces = APPLICATION_JSON + "," + APPLICATION_XML
)
@Path("/ea_ids")
@Consumes({ APPLICATION_JSON, APPLICATION_XML })
@Produces({ APPLICATION_JSON, APPLICATION_XML })
@Component
public class EaIdResource {

    @Context
    private UriInfo uriInfo;


    /**
     * List the available ea_ids (permits and authorizations) for which you are authorized
     *
     * @param preconditions conditional request structure
     * @return a response containing an {@link EntityReferenceListResponse} entity
     * @throws Exception if the request cannot be completed normally.
     */
    @GET
    @ApiOperation(value = "List the available ea-id's",
            notes = "This operation will list all ea-id's (permits and authorizations) for which the user of the API is authorized "
    )
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "OK", response = EntityReferenceListResponse.class),
            @ApiResponse(code = 304, message = "Not Modified - see conditional request documentation")
    })
    public Response listEaIds(@BeanParam Preconditions preconditions) throws Exception {
        //User user = datasetService.getSystemUser();
        //List<DatasetEntity> datasets = datasetService.getDatasets(user);

        //TODO replace with real entity
        List<EaId> eaIds = new ArrayList<>();
        for (String id : new String[] { "id1/001", "id2-002", "id 003" }) {
            EaId eaId = new EaId();
            eaId.setId(id);
            eaIds.add(eaId);
        }

        return onPreconditionsPass(eaIds, preconditions,
                () -> {
                    List<EntityReference> entityReferences = eaIds.stream()
                            .map(e -> new EntityReference(e.getId(), Linker.info(uriInfo).eaId(e.getId())))
                            .collect(Collectors.toList());
                    return new EntityReferenceListResponse(entityReferences,
                            null, //TODO - need the operator change date
                            null // TODO - need the e-tag from referring entity (operator)
                    ).toResponseBuilder();
                }).build();
    }

    /**
     * Retrieve permit details
     *
     * @return a response containing an {@link DatasetEntityResponse} entity
     * @throws Exception if the request cannot be completed normally.
     */
    @GET
    @Path("/{ea_id}")
    @ApiOperation(value = "Retrieve permit or authorization details",
            notes = "**Retrieve the details for the given `ea_id`**"
    )
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "OK", response = DatasetEntityResponse.class),
            @ApiResponse(code = 304, message = "Not Modified - see conditional request documentation"),
            @ApiResponse(
                    code = 404,
                    message = "Not Found - The `ea_id` parameter did not match a known resource",
                    response = ErrorResponse.class
            )
    })
    public Response getEaId(
            @PathParam("ea_id")
            @Pattern(regexp = "\\p{Print}+")
            @ApiParam("The unique identifier for the permit or authorization") final String eaIdId,
            @BeanParam Preconditions preconditions)
            throws Exception {

        EaId eaId = fromEntity(eaIdId);

        return onEaId(eaId, eaIdEntityResponseBuilder -> onPreconditionsPass(eaId, preconditions, () ->
            new EaIdEntityResponse(Response.Status.OK, eaId).toResponseBuilder()
        )).build();
    }

    private Response.ResponseBuilder onEaId(EaId eaId, Function<EaId, Response.ResponseBuilder> handler) {
        return (eaId == null) ? ErrorResponse.EA_ID_NOT_FOUND.toResponseBuilder() : handler.apply(eaId);
    }

    // TODO - get the actual EaId data from the service layer
    private EaId fromEntity(String eaIdId) {
        EaId eaId = new EaId();
        eaId.setId(eaIdId);
        Linker.info(uriInfo).resolve(eaId);
        return eaId;
    }
}

