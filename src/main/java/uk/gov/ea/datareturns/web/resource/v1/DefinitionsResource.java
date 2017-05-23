package uk.gov.ea.datareturns.web.resource.v1;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.annotations.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import uk.gov.ea.datareturns.domain.jpa.dao.userdata.impl.ValidationErrorDao;
import uk.gov.ea.datareturns.domain.jpa.entities.masterdata.impl.ControlledListsList;
import uk.gov.ea.datareturns.domain.processors.ControlledListProcessor;
import uk.gov.ea.datareturns.web.resource.ControlledListResource;
import uk.gov.ea.datareturns.web.resource.v1.model.common.Linker;
import uk.gov.ea.datareturns.web.resource.v1.model.common.Preconditions;
import uk.gov.ea.datareturns.web.resource.v1.model.common.references.EntityReference;
import uk.gov.ea.datareturns.web.resource.v1.model.definitions.ConstraintDefinition;
import uk.gov.ea.datareturns.web.resource.v1.model.definitions.FieldDefinition;
import uk.gov.ea.datareturns.web.resource.v1.model.record.payload.Payload;
import uk.gov.ea.datareturns.web.resource.v1.model.response.*;

import javax.inject.Inject;
import javax.validation.constraints.Pattern;
import javax.ws.rs.*;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;
import java.lang.reflect.Field;
import java.util.*;
import java.util.stream.Collectors;

import static javax.ws.rs.core.MediaType.APPLICATION_JSON;
import static javax.ws.rs.core.MediaType.APPLICATION_XML;

/**
 * RESTful resource to manage dataset entities.
 *
 * @author Sam Gardner-Dell
 */
@Api(description = "Definitions Resource",
        tags = { "Definitions" },
        // Specifying consumes/produces (again) here allows us to default the swagger ui to json
        consumes = APPLICATION_JSON + "," + APPLICATION_XML,
        produces = APPLICATION_JSON + "," + APPLICATION_XML
)
@Path("/definitions")
@Consumes({ APPLICATION_JSON, APPLICATION_XML })
@Produces({ APPLICATION_JSON, APPLICATION_XML })
@Component
@Scope(ConfigurableBeanFactory.SCOPE_SINGLETON)
public class DefinitionsResource {
    private static final Logger LOGGER = LoggerFactory.getLogger(DefinitionsResource.class);

    @Context
    private UriInfo uriInfo;

    /** controlled list processor */
    private final ControlledListProcessor controlledListProcessor;
    /** controlled list processor */
    private final ValidationErrorDao validationErrorDao;

    /**
     * Create a new {@link ControlledListResource} RESTful service
     *
     * @param controlledListProcessor the controlled list processor
     */
    @Inject
    public DefinitionsResource(final ControlledListProcessor controlledListProcessor, final ValidationErrorDao validationErrorDao) {
        this.controlledListProcessor = controlledListProcessor;
        this.validationErrorDao = validationErrorDao;
    }

    /**
     * List payload types
     *
     * @return a response containing an {@link PayloadListResponse} entity
     * @throws Exception if the request cannot be completed normally.
     */
    @GET
    @ApiOperation(value = "List payload types",
            notes = "This operation will list all payload types available via the API",
            response = PayloadListResponse.class
    )
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "OK", response = PayloadListResponse.class)
    })
    public Response listPayloads() throws Exception {
        PayloadListResponse response = new PayloadListResponse(Payload.TYPES.keySet());
        return response.toResponseBuilder().build();
    }

    /**
     * Retrieve the list of fields available for the given payload_type
     *
     * @param payloadType the payload type for which to return fields
     * @param preconditions conditional request structure
     * @return a response containing an {@link EntityListResponse} entity
     * @throws Exception if the request cannot be completed normally.
     */
    @GET
    @Path("/{payload_type}/fields")
    @ApiOperation(value = "List fields",
            notes = "List the fields for a given `payload type`"
    )
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "OK", response = EntityListResponse.class),
            @ApiResponse(
                    code = 404,
                    message = "Not Found - The `payload_type` parameter did not match a known payload type.",
                    response = ErrorResponse.class
            )
    })
    public Response listFields(
            @PathParam("payload_type")
            @Pattern(regexp = "[A-Za-z0-9_-]+")
            @ApiParam("The payload type for which to return fields") final String payloadType,
            @BeanParam Preconditions preconditions)
            throws Exception {

        Class<?> payloadClass = Payload.TYPES.get(payloadType);

        Response.ResponseBuilder rb;
        if (payloadClass == null) {
            rb = ErrorResponse.PAYLOAD_TYPE_NOT_FOUND.toResponseBuilder();
        } else {
            List<EntityReference> refs = new ArrayList<>();
            Collection<String> fields = getFields(payloadClass).keySet();
            for (String fieldId : fields) {
                refs.add(new EntityReference(fieldId, Linker.info(uriInfo).field(payloadType, fieldId)));
            }
            EntityListResponse response = new EntityListResponse(refs);
            rb = response.toResponseBuilder();
        }
        return rb.build();
    }

    /**
     * Retrieve the definition for the given field_id and payload_type
     *
     * @param payloadType the payload type for which to return fields
     * @param fieldId the name of the field for which to retrieve the definition
     * @param preconditions conditional request structure
     * @return a response containing an {@link FieldDefinitionResponse} entity
     * @throws Exception if the request cannot be completed normally.
     */
    @GET
    @Path("/{payload_type}/fields/{field_id}")
    @ApiOperation(value = "Retrieve field definition",
            notes = "Retrieve the definition for the given field"
    )
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "OK", response = FieldDefinitionResponse.class),
            @ApiResponse(code = 304, message = "Not Modified - see conditional request documentation"),
            @ApiResponse(
                    code = 404,
                    message = "Not Found - The `payload_type` or `field_id` parameter did not match a known resource - "
                            + "see the `meta` structure in the response envelope for more detail",
                    response = ErrorResponse.class
            )
    })
    public Response getFieldDefinition(
            @PathParam("payload_type")
            @Pattern(regexp = "[A-Za-z0-9_-]+")
            @ApiParam("The payload type associated with the target field") final String payloadType,
            @PathParam("field_id")
            @Pattern(regexp = "[A-Za-z0-9_-]+")
            @ApiParam("The name of the field defined for the given payload type") final String fieldId,
            @BeanParam Preconditions preconditions)
            throws Exception {

        // TODO: IMPLEMENT CONDITIONAL REQUEST HANDLING FOR FIELD ENTITIES
        Class<?> payloadClass = Payload.TYPES.get(payloadType);

        Response.ResponseBuilder rb;
        if (payloadClass == null) {
            rb = ErrorResponse.PAYLOAD_TYPE_NOT_FOUND.toResponseBuilder();
        } else {
            Map<String, Field> fields = getFields(payloadClass);
            Field field = fields.get(fieldId);

            if (field == null) {
                rb = ErrorResponse.FIELD_NOT_FOUND.toResponseBuilder();
            } else {
                FieldDefinition definition = new FieldDefinition();
                definition.setId(fieldId);
                // TODO: Description
                definition.setDescription("A useful description for " + fieldId);
                definition.setType(field.getType().getSimpleName());

                ControlledListsList controlledList = ControlledListsList.getByPath(fieldId);
                // Check we have a registered controlled list type
                if (controlledList != null) {
                    definition.setAllowed(controlledListProcessor.getListData(controlledList, null).getRight());
                }

                FieldDefinitionResponse response = new FieldDefinitionResponse(Response.Status.OK, definition);
                rb = response.toResponseBuilder();
            }
        }
        return rb.build();
    }

    /**
     * List the potential validation constraints for a particular payload_type
     *
     * @param payloadType the payload type for which to return fields
     * @return a response containing an {@link EntityListResponse} entity
     * @throws Exception if the request cannot be completed normally.
     */
    @GET
    @Path("/{payload_type}/constraints")
    @ApiOperation(value = "List constraints",
            notes = "List the potential validation constraints for the payload identified by the given `payload_type`"
    )
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "OK", response = EntityListResponse.class),
            @ApiResponse(
                    code = 404,
                    message = "Not Found - The `payload_type` parameter did not match a payload type.",
                    response = ErrorResponse.class
            )
    })
    public Response listValidationConstraints(
            @PathParam("payload_type")
            @Pattern(regexp = "[A-Za-z0-9_-]+")
            @ApiParam("The payload type the target field belongs to.") final String payloadType)
            throws Exception {

        // TODO: Graham, service layer needs to support different payload types.
        Class<?> payloadClass = Payload.TYPES.get(payloadType);

        Response.ResponseBuilder rb;
        if (payloadClass == null) {
            rb = ErrorResponse.PAYLOAD_TYPE_NOT_FOUND.toResponseBuilder();
        } else {
            List<EntityReference> refs = validationErrorDao.list().stream()
                    .map((constraint) ->
                            new EntityReference(constraint.getError(), Linker.info(uriInfo).constraint(payloadType, constraint.getError()))
                    )
                    .collect(Collectors.toList());
            rb = new EntityListResponse(refs).toResponseBuilder();
        }
        return rb.build();
    }

    /**
     * Retrieve the list of potential validation constraints for a particular payload_type
     *
     * @param payloadType the payload type for which to return the constraint definitions
     * @param constraintId the ID of the constraint for which the definition should be returned
     * @return a response containing an {@link ConstraintDefinitionResponse} entity
     * @throws Exception if the request cannot be completed normally.
     */
    @GET
    @Path("/{payload_type}/constraints/{constraint_id}")
    @ApiOperation(
            value = "Retrieve constraint definition",
            notes = "Retrieve the validation constraint definition for the constraint identified by the given `constraint_id` "
                    + "and `payload_type`"
    )
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "OK", response = ConstraintDefinitionResponse.class),
            @ApiResponse(
                    code = 404,
                    message = "Not Found - The `payload_type` or `constraint_id` parameter did not match a known resource - "
                            + "see the `meta` structure in the response envelope for more detail",
                    response = ErrorResponse.class
            )
    })
    public Response getValidationContraint(
            @PathParam("payload_type")
            @Pattern(regexp = "[A-Za-z0-9_-]+")
            @ApiParam("The payload type the target contraint belongs to.") final String payloadType,
            @PathParam("constraint_id")
            @Pattern(regexp = "[A-Za-z0-9_-]+")
            @ApiParam("The unique identifier for the target contraint.") final String constraintId
    )
            throws Exception {

        // TODO: Graham, service layer needs to support different payload types.
        Class<?> payloadClass = Payload.TYPES.get(payloadType);

        Response.ResponseBuilder rb;
        if (payloadClass == null) {
            rb = ErrorResponse.PAYLOAD_TYPE_NOT_FOUND.toResponseBuilder();
        } else {
            ConstraintDefinition definition = validationErrorDao.list().stream()
                    .filter((constraint) -> constraint.getError().equals(constraintId))
                    .findFirst()
                    .map((constraint) -> {
                        ConstraintDefinition def = new ConstraintDefinition();
                        def.setId(constraint.getError());
                        def.setDescription(constraint.getMessage());
                        def.setFields(constraint.getFields().stream()
                                .map((fieldId) -> new EntityReference(fieldId, Linker.info(uriInfo).field(payloadType, fieldId)))
                                .collect(Collectors.toList()));
                        return def;
                    })
                    .orElse(null);

            if (definition == null) {
                rb = ErrorResponse.CONSTRAINT_NOT_FOUND.toResponseBuilder();
            } else {
                rb = new ConstraintDefinitionResponse(Response.Status.OK, definition).toResponseBuilder();
            }
        }
        return rb.build();
    }

    private Map<String, Field> getFields(Class<?> payloadClass) {
        Map<String, Field> fields = new LinkedHashMap<>();

        for (Field f : payloadClass.getDeclaredFields()) {
            JsonProperty mapping = f.getAnnotation(JsonProperty.class);
            if (mapping != null) {
                fields.put(mapping.value(), f);
            }
        }
        return fields;
    }
}