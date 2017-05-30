package uk.gov.ea.datareturns.web.resource.v1;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.annotations.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import uk.gov.ea.datareturns.domain.jpa.dao.userdata.impl.PayloadTypeDao;
import uk.gov.ea.datareturns.domain.jpa.dao.userdata.impl.ValidationErrorDao;
import uk.gov.ea.datareturns.domain.jpa.entities.masterdata.impl.ControlledListsList;
import uk.gov.ea.datareturns.domain.jpa.entities.userdata.impl.PayloadType;
import uk.gov.ea.datareturns.domain.jpa.entities.userdata.impl.ValidationError;
import uk.gov.ea.datareturns.domain.jpa.entities.userdata.impl.ValidationErrorId;
import uk.gov.ea.datareturns.domain.processors.ControlledListProcessor;
import uk.gov.ea.datareturns.web.resource.ControlledListResource;
import uk.gov.ea.datareturns.web.resource.v1.model.common.Linker;
import uk.gov.ea.datareturns.web.resource.v1.model.common.Preconditions;
import uk.gov.ea.datareturns.web.resource.v1.model.common.references.EntityReference;
import uk.gov.ea.datareturns.web.resource.v1.model.common.references.PayloadReference;
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
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

import static javax.ws.rs.core.MediaType.APPLICATION_JSON;
import static javax.ws.rs.core.MediaType.APPLICATION_XML;

/**
 * RESTful resource to provide business object definitions
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
    /** Payload types */
    private final PayloadTypeDao payloadTypeDao;

    /**
     * Create a new {@link ControlledListResource} RESTful service
     *
     * @param controlledListProcessor the controlled list processor
     */
    @Inject
    public DefinitionsResource(final ControlledListProcessor controlledListProcessor,
                               final ValidationErrorDao validationErrorDao,
                               final PayloadTypeDao payloadTypeDao) {

        this.controlledListProcessor = controlledListProcessor;
        this.validationErrorDao = validationErrorDao;
        this.payloadTypeDao = payloadTypeDao;
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
        return new PayloadListResponse(
                Payload.TYPES.keySet().stream()
                        .map((type) -> {
                            PayloadReference ref = new PayloadReference(type);
                            Linker.info(uriInfo).resolve(ref);
                            return ref;
                        })
                        .collect(Collectors.toList())
        ).toResponseBuilder().build();
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
        return onPayloadType(payloadType, (payloadClass) ->
                new EntityListResponse(
                        getFields(payloadClass).keySet().stream()
                                .map((fieldId) -> new EntityReference(fieldId, Linker.info(uriInfo).field(payloadType, fieldId)))
                                .collect(Collectors.toList())
                ).toResponseBuilder()
        ).build();
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
        return onPayloadType(payloadType, (payloadClass) ->
                onField(payloadClass, fieldId, (fieldDefinition) ->
                        new FieldDefinitionResponse(Response.Status.OK, fieldDefinition).toResponseBuilder())
        ).build();
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

        return onPayloadType(payloadType, (payloadClass) ->
                new EntityListResponse(
                        // TODO: Graham, service layer needs to support different payload types.
                        validationErrorDao.list().stream()
                                .map((constraint) -> {
                                    String uri = Linker.info(uriInfo).constraint(payloadType, constraint.getId().getError());
                                    return new EntityReference(constraint.getId().getError(), uri);
                                })
                                .collect(Collectors.toList())
                ).toResponseBuilder()
        ).build();
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
        return onPayloadType(payloadType, (payloadClass) ->
                onConstraint(payloadType, constraintId, (constraintDefinition) ->
                        new ConstraintDefinitionResponse(constraintDefinition).toResponseBuilder()
                )
        ).build();
    }

    private Response.ResponseBuilder onPayloadType(String payloadType, Function<Class<?>, Response.ResponseBuilder> handler) {
        Class<?> payloadClass = Payload.TYPES.get(payloadType);
        return (payloadClass == null) ? ErrorResponse.PAYLOAD_TYPE_NOT_FOUND.toResponseBuilder() : handler.apply(payloadClass);
    }

    private Response.ResponseBuilder onConstraint(String payloadTypeStr, String constraintId, Function<ConstraintDefinition, Response
            .ResponseBuilder> handler) {

        PayloadType payloadType = payloadTypeDao.get(payloadTypeStr);
        ValidationErrorId id = new ValidationErrorId();
        id.setPayloadType(payloadType);
        id.setError(constraintId);
        ValidationError validationError = validationErrorDao.get(id);
        ConstraintDefinition definition = new ConstraintDefinition();
        definition.setId(validationError.getId().getError());
        definition.setDescription(validationError.getMessage());
        definition.setFields(validationError.getFields().stream()
                            .map((field) -> new EntityReference(field.getId().getFieldName(), Linker.info(uriInfo).field(
                                    payloadType.getPayloadTypeName(), field.getId().getFieldName())))
                            .collect(Collectors.toList()));

        return (definition == null) ? ErrorResponse.CONSTRAINT_NOT_FOUND.toResponseBuilder() : handler.apply(definition);
    }

    private Response.ResponseBuilder onField(Class<?> payloadClass, String fieldId, Function<FieldDefinition, Response
            .ResponseBuilder> handler) {
        Map<String, Field> fields = getFields(payloadClass);
        Field field = fields.get(fieldId);

        Response.ResponseBuilder rb;
        if (field == null) {
            rb = ErrorResponse.FIELD_NOT_FOUND.toResponseBuilder();
        } else {
            FieldDefinition definition = new FieldDefinition();
            definition.setId(fieldId);
            definition.setDescription("A useful description for " + fieldId);
            definition.setType(field.getType().getSimpleName());

            ControlledListsList controlledList = ControlledListsList.getByPath(fieldId);
            // Check we have a registered controlled list type
            if (controlledList != null) {
                definition.setAllowed(controlledListProcessor.getListData(controlledList, null).getRight());
            }

            rb = handler.apply(definition);
        }
        return rb;
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