package uk.gov.ea.datareturns.web.resource.v1;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.annotations.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import uk.gov.ea.datareturns.domain.jpa.entities.masterdata.impl.ControlledListsList;
import uk.gov.ea.datareturns.domain.jpa.entities.userdata.impl.*;
import uk.gov.ea.datareturns.domain.jpa.repositories.systemdata.FieldRepository;
import uk.gov.ea.datareturns.domain.jpa.repositories.systemdata.PayloadTypeRepository;
import uk.gov.ea.datareturns.domain.jpa.repositories.systemdata.ValidationConstraintRepository;
import uk.gov.ea.datareturns.domain.processors.ControlledListProcessor;
import uk.gov.ea.datareturns.web.resource.ControlledListResource;
import uk.gov.ea.datareturns.web.resource.JerseyResource;
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
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

import static javax.ws.rs.core.MediaType.APPLICATION_JSON;
import static javax.ws.rs.core.MediaType.APPLICATION_XML;
import static uk.gov.ea.datareturns.web.resource.v1.model.common.PreconditionChecks.onPreconditionsPass;

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
public class DefinitionsResource implements JerseyResource {
    private static final Logger LOGGER = LoggerFactory.getLogger(DefinitionsResource.class);

    @Context
    private UriInfo uriInfo;

    /** controlled list processor */
    private final ControlledListProcessor controlledListProcessor;
    /** controlled list processor */
    private final ValidationConstraintRepository validationConstraintRepository;
    /** Payload types */
    private final PayloadTypeRepository payloadTypeRepository;
    /** Fields **/
    private final FieldRepository fieldRepository;

    /**
     * Create a new {@link ControlledListResource} RESTful service
     *
     * @param controlledListProcessor the controlled list processor
     */
    @Inject
    public DefinitionsResource(final ControlledListProcessor controlledListProcessor,
            final ValidationConstraintRepository validationConstraintRepository,
            final PayloadTypeRepository payloadTypeRepository,
            final FieldRepository fieldRepository) {

        this.controlledListProcessor = controlledListProcessor;
        this.validationConstraintRepository = validationConstraintRepository;
        this.payloadTypeRepository = payloadTypeRepository;
        this.fieldRepository = fieldRepository;
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
     * @return a response containing an {@link EntityReferenceListResponse} entity
     * @throws Exception if the request cannot be completed normally.
     */
    @GET
    @Path("/{payload_type}/fields")
    @ApiOperation(value = "List fields",
            notes = "List the fields for a given `payload type`"
    )
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "OK", response = EntityReferenceListResponse.class),
            @ApiResponse(code = 304, message = "Not Modified - see conditional request documentation"),
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
        return onPayloadType(payloadType, (payloadClass) -> {
            PayloadType payloadEntityType = payloadTypeRepository.getOne(payloadType);
            return onPreconditionsPass(payloadEntityType, preconditions, () -> {
                Map<String, Field> fieldMap = getFields(payloadClass);
                return new EntityReferenceListResponse(
                        fieldMap.keySet().stream()
                                .map((fieldId) -> new EntityReference(fieldId, Linker.info(uriInfo).field(payloadType, fieldId)))
                                .collect(Collectors.toList()),
                        Date.from(payloadEntityType.getLastChangedDate()),
                        Preconditions.createEtag(payloadEntityType.getLastChangedDate())
                ).toResponseBuilder();
            });
        }).build();
    }

    /**
     * Retrieve the list of fields available for the given payload_type
     *
     * @param payloadType the payload type for which to return fields
     * @param preconditions conditional request structure
     * @return a response containing an {@link FieldDefinitionListResponse} entity
     * @throws Exception if the request cannot be completed normally.
     */
    @GET
    @Path("/{payload_type}/fields/$data")
    @ApiOperation(value = "List field data",
            notes = "List the field data for a given `payload type`"
    )
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "OK", response = FieldDefinitionListResponse.class),
            @ApiResponse(code = 304, message = "Not Modified - see conditional request documentation"),
            @ApiResponse(
                    code = 404,
                    message = "Not Found - The `payload_type` parameter did not match a known payload type.",
                    response = ErrorResponse.class
            )
    })
    public Response listFieldData(
            @PathParam("payload_type")
            @Pattern(regexp = "[A-Za-z0-9_-]+")
            @ApiParam("The payload type for which to return fields") final String payloadType,
            @BeanParam Preconditions preconditions)
            throws Exception {
        return onPayloadType(payloadType, (payloadClass) -> {
            PayloadType payloadEntityType = payloadTypeRepository.getOne(payloadType);
            return onPreconditionsPass(payloadEntityType, preconditions, () -> {
                Map<String, Field> fieldMap = getFields(payloadClass);
                List<FieldDefinition> defs = fieldMap.entrySet().stream()
                        .map((mapEntry) -> {
                            FieldEntity fieldEntity = fieldRepository.getOne(new FieldId(mapEntry.getKey(), payloadEntityType));
                            return toFieldDefinition(payloadEntityType, mapEntry.getValue(), fieldEntity);
                        })
                        .collect(Collectors.toList());
                return new FieldDefinitionListResponse(defs,
                        Date.from(payloadEntityType.getLastChangedDate()),
                        Preconditions.createEtag(payloadEntityType.getLastChangedDate())
                ).toResponseBuilder();
            });
        }).build();
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
    @Path("/{payload_type}/fields/{field_id : [a-zA-Z0-9_-]+ }")
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
     * @return a response containing an {@link EntityReferenceListResponse} entity
     * @throws Exception if the request cannot be completed normally.
     */
    @GET
    @Path("/{payload_type}/constraints")
    @ApiOperation(value = "List constraints",
            notes = "List the potential validation constraints for the payload identified by the given `payload_type`"
    )
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "OK", response = EntityReferenceListResponse.class),
            @ApiResponse(code = 304, message = "Not Modified - see conditional request documentation"),
            @ApiResponse(
                    code = 404,
                    message = "Not Found - The `payload_type` parameter did not match a payload type.",
                    response = ErrorResponse.class
            )
    })
    public Response listValidationConstraints(
            @PathParam("payload_type")
            @Pattern(regexp = "[A-Za-z0-9_-]+")
            @ApiParam("The payload type the target field belongs to.") final String payloadType,
            @BeanParam Preconditions preconditions)
            throws Exception {
        return onPayloadType(payloadType, (payloadClass) -> {
            PayloadType payloadEntityType = payloadTypeRepository.getOne(payloadType);
            return onPreconditionsPass(payloadEntityType, preconditions, () -> {
                List<ValidationError> errors = validationConstraintRepository.findAllByIdPayloadType(payloadEntityType);

                return new EntityReferenceListResponse(
                        errors.stream()
                                .map((constraint) -> {
                                    String uri = Linker.info(uriInfo).constraint(payloadType, constraint.getId().getError());
                                    return new EntityReference(constraint.getId().getError(), uri);
                                })
                                .collect(Collectors.toList()),
                        Date.from(payloadEntityType.getLastChangedDate()),
                        Preconditions.createEtag(payloadEntityType.getLastChangedDate())
                ).toResponseBuilder();
            });
        }).build();
    }

    /**
     * Retrieve full information about all validation constraints for a particular payload_type
     *
     * @param payloadType the payload type for which to return fields
     * @return a response containing an {@link ConstraintDefinitionListResponse} entity
     * @throws Exception if the request cannot be completed normally.
     */
    @GET
    @Path("/{payload_type}/constraints/$data")
    @ApiOperation(value = "Retrieve constraint data",
            notes = "Retrieve all validation constraint information for the given `payload_type`"
    )
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "OK", response = ConstraintDefinitionListResponse.class),
            @ApiResponse(code = 304, message = "Not Modified - see conditional request documentation"),
            @ApiResponse(
                    code = 404,
                    message = "Not Found - The `payload_type` parameter did not match a payload type.",
                    response = ErrorResponse.class
            )
    })
    public Response listValidationConstraintData(
            @PathParam("payload_type")
            @Pattern(regexp = "[A-Za-z0-9_-]+")
            @ApiParam("The payload type the target field belongs to.") final String payloadType,
            @BeanParam Preconditions preconditions)
            throws Exception {
        return onPayloadType(payloadType, (payloadClass) -> {
            PayloadType payloadEntityType = payloadTypeRepository.getOne(payloadType);
            return onPreconditionsPass(payloadEntityType, preconditions, () -> {
                List<ValidationError> errors = validationConstraintRepository.findAllByIdPayloadType(payloadEntityType);
                return new ConstraintDefinitionListResponse(
                        errors.stream()
                                .map((validationErrorDef) -> toConstraint(payloadEntityType, validationErrorDef))
                                .collect(Collectors.toList()),
                        Date.from(payloadEntityType.getLastChangedDate()),
                        Preconditions.createEtag(payloadEntityType.getLastChangedDate())
                ).toResponseBuilder();
            });
        }).build();
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
    @Path("/{payload_type}/constraints/{constraint_id : [a-zA-Z0-9_-]+ }")
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
    public Response getValidationConstraint(
            @PathParam("payload_type")
            @Pattern(regexp = "[A-Za-z0-9_-]+")
            @ApiParam("The payload type the target constraint belongs to.") final String payloadType,
            @PathParam("constraint_id")
            @Pattern(regexp = "[A-Za-z0-9_-]+")
            @ApiParam("The unique identifier for the target constraint.") final String constraintId
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
        PayloadType payloadType = payloadTypeRepository.getOne(payloadTypeStr);
        ValidationErrorId id = new ValidationErrorId(payloadType, constraintId);
        ValidationError validationError = validationConstraintRepository.getOne(id);
        if (validationError == null) {
            return ErrorResponse.CONSTRAINT_NOT_FOUND.toResponseBuilder();
        } else {
            return handler.apply(toConstraint(payloadType, validationError));
        }
    }

    private Response.ResponseBuilder onField(Class<?> payloadClass, String fieldId, Function<FieldDefinition, Response
            .ResponseBuilder> handler) {

        Map<String, Field> fields = getFields(payloadClass);
        Field field = fields.get(fieldId);
        String payloadTypeStr = Payload.NAMES.get(payloadClass);
        PayloadType payloadEntityType = payloadTypeRepository.getOne(payloadTypeStr);
        FieldEntity fieldEntity = fieldRepository.getOne(new FieldId(fieldId, payloadEntityType));

        Response.ResponseBuilder rb;
        if (field == null || fieldEntity == null) {
            rb = ErrorResponse.FIELD_NOT_FOUND.toResponseBuilder();
        } else {
            rb = handler.apply(toFieldDefinition(payloadEntityType, field, fieldEntity));
        }
        return rb;
    }

    private FieldDefinition toFieldDefinition(PayloadType payloadType, Field field, FieldEntity fieldEntity) {
        FieldDefinition definition = new FieldDefinition();
        definition.setId(fieldEntity.getId().getFieldName());
        definition.setDescription(fieldEntity.getDescription());
        definition.setType(field.getType().getSimpleName());

        ControlledListsList controlledList = ControlledListsList.getByPath(fieldEntity.getId().getFieldName());
        // Check we have a registered controlled list type
        if (controlledList != null) {
            definition.setAllowed(controlledListProcessor.getListData(controlledList, null).getRight());
        }
        return definition;
    }

    private ConstraintDefinition toConstraint(PayloadType payloadType, ValidationError validationError) {
        ConstraintDefinition definition = new ConstraintDefinition();
        definition.setId(validationError.getId().getError());
        definition.setDescription(validationError.getMessage());
        definition.setFields(validationError.getFields().stream()
                .map((field) -> new EntityReference(field.getId().getFieldName(), Linker.info(uriInfo).field(
                        payloadType.getPayloadTypeName(), field.getId().getFieldName())))
                .collect(Collectors.toList()));
        return definition;
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