package uk.gov.ea.datareturns.web.resource.v1;

import io.swagger.annotations.*;
import org.springframework.stereotype.Component;
import uk.gov.ea.datareturns.domain.jpa.entities.masterdata.impl.UniqueIdentifier;
import uk.gov.ea.datareturns.domain.jpa.entities.masterdata.impl.UniqueIdentifierAlias;
import uk.gov.ea.datareturns.domain.jpa.repositories.masterdata.UniqueIdentifierRepository;
import uk.gov.ea.datareturns.domain.jpa.service.SitePermitService;
import uk.gov.ea.datareturns.web.resource.JerseyResource;
import uk.gov.ea.datareturns.web.resource.v1.model.common.Linker;
import uk.gov.ea.datareturns.web.resource.v1.model.common.Preconditions;
import uk.gov.ea.datareturns.web.resource.v1.model.common.references.EntityReference;
import uk.gov.ea.datareturns.web.resource.v1.model.eaid.EaId;
import uk.gov.ea.datareturns.web.resource.v1.model.response.*;

import javax.validation.constraints.Pattern;
import javax.ws.rs.*;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Date;
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
public class EaIdResource implements JerseyResource {

    @Context
    private UriInfo uriInfo;

    private final UniqueIdentifierRepository uniqueIdentifierRepository;
    private final SitePermitService sitePermitService;

    // This is a placeholder for holding the change date of the set of permits
    // (currently the permits are not modified)
    private final static Instant PERMIT_CHANGE_FLOOR = Instant.parse("2017-01-01T00:00:00.000Z");
    //private final static Instant PERMIT_CHANGE_FLOOR = LocalDate.of(1970, 01, 01);

    public EaIdResource(
            UniqueIdentifierRepository uniqueIdentifierRepository,
            SitePermitService sitePermitService) {
        this.uniqueIdentifierRepository = uniqueIdentifierRepository;
        this.sitePermitService = sitePermitService;
    }

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
        return onPreconditionsPass(Date.from(PERMIT_CHANGE_FLOOR), preconditions,
                () -> {
                    List<EntityReference> entityReferences = uniqueIdentifierRepository.findAll().stream()
                            .map(eaId ->
                                    new EntityReference(eaId.getName(),
                                            Linker.info(uriInfo).eaId(eaId.getName())))
                            .collect(Collectors.toList());
                    return new EntityReferenceListResponse(entityReferences,
                            Date.from(PERMIT_CHANGE_FLOOR),
                            Preconditions.createEtag(PERMIT_CHANGE_FLOOR)
                    ).toResponseBuilder();
                }).build();
    }

    /**
     * Get all the entity data (including sites and aliases)
     * @return
     * @throws Exception
     */
    @GET
    @Path("/$data")
    @ApiOperation(value = "The full list the available ea-id's sites and aliases",
            notes = "This operation will list all ea-id's (permits and authorizations) for which the user of the API is authorized "
    )
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "OK", response = EaIdListResponse.class),
            @ApiResponse(code = 304, message = "Not Modified - see conditional request documentation")
    })
    public Response listEaIdData(@BeanParam Preconditions preconditions)
            throws Exception {
        return onPreconditionsPass(Date.from(PERMIT_CHANGE_FLOOR), preconditions, () -> {
            List<EaId> eaIds = new ArrayList<>();
            for (UniqueIdentifier uniqueIdentifier : uniqueIdentifierRepository.findAll()) {
                EaId eaId = new EaId();
                eaId.setId(uniqueIdentifier.getName());
                eaId.setAliases(uniqueIdentifier.getAliases().stream().map(UniqueIdentifierAlias::getName).collect(Collectors.toSet()));
                eaId.setSiteName(uniqueIdentifier.getSite().getName());
                eaIds.add(eaId);
            }

            EaIdListResponse eaIdListResponse = new EaIdListResponse(eaIds,
                    Date.from(PERMIT_CHANGE_FLOOR),
                    Preconditions.createEtag(PERMIT_CHANGE_FLOOR));

            return eaIdListResponse.toResponseBuilder();

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

        final UniqueIdentifier uniqueIdentifier = sitePermitService.getUniqueIdentifierByName(eaIdId);

        final EaId eaId = fromEntity(uniqueIdentifier);
        return onEaId(eaId, eaIdEntityResponseBuilder -> onPreconditionsPass(eaId, preconditions, () ->
                new EaIdEntityResponse(Response.Status.OK, eaId).toResponseBuilder()
        )).build();
    }

    private Response.ResponseBuilder onEaId(final EaId eaId, Function<EaId, Response.ResponseBuilder> handler) {
        return (eaId == null) ? ErrorResponse.EA_ID_NOT_FOUND.toResponseBuilder() : handler.apply(eaId);
    }

    private EaId fromEntity(UniqueIdentifier uniqueIdentifier) {
        if (uniqueIdentifier != null) {
            EaId eaId = new EaId();
            eaId.setId(uniqueIdentifier.getName());
            eaId.setSiteName(uniqueIdentifier.getSite().getName());

            eaId.setAliases(uniqueIdentifier.getAliases()
                    .stream()
                    .map(UniqueIdentifierAlias::getName)
                    .distinct()
                    .collect(Collectors.toSet()));

            Linker.info(uriInfo).resolve(eaId);

            return eaId;
        }

        return null;
    }
}

