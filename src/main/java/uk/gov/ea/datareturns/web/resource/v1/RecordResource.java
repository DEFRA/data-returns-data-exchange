package uk.gov.ea.datareturns.web.resource.v1;

import io.swagger.annotations.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import uk.gov.ea.datareturns.config.SubmissionConfiguration;
import uk.gov.ea.datareturns.domain.jpa.entities.userdata.impl.DatasetEntity;
import uk.gov.ea.datareturns.domain.jpa.entities.userdata.impl.RecordEntity;
import uk.gov.ea.datareturns.domain.jpa.service.DatasetService;
import uk.gov.ea.datareturns.domain.jpa.service.SubmissionService;
import uk.gov.ea.datareturns.web.resource.v1.model.common.Linker;
import uk.gov.ea.datareturns.web.resource.v1.model.common.Preconditions;
import uk.gov.ea.datareturns.web.resource.v1.model.common.references.EntityReference;
import uk.gov.ea.datareturns.web.resource.v1.model.record.Record;
import uk.gov.ea.datareturns.web.resource.v1.model.record.RecordAdaptor;
import uk.gov.ea.datareturns.web.resource.v1.model.record.payload.DataSamplePayload;
import uk.gov.ea.datareturns.web.resource.v1.model.record.payload.Payload;
import uk.gov.ea.datareturns.web.resource.v1.model.request.BatchRecordRequest;
import uk.gov.ea.datareturns.web.resource.v1.model.request.BatchRecordRequestItem;
import uk.gov.ea.datareturns.web.resource.v1.model.response.EntityListResponse;
import uk.gov.ea.datareturns.web.resource.v1.model.response.ErrorResponse;
import uk.gov.ea.datareturns.web.resource.v1.model.response.MultiStatusResponse;
import uk.gov.ea.datareturns.web.resource.v1.model.response.RecordEntityResponse;

import javax.annotation.Resource;
import javax.inject.Inject;
import javax.validation.constraints.Pattern;
import javax.ws.rs.*;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;
import java.util.*;
import java.util.stream.Collectors;

import static javax.ws.rs.core.MediaType.APPLICATION_JSON;
import static javax.ws.rs.core.MediaType.APPLICATION_XML;

/**
 * RESTful resource to manage record entities.
 *
 * @author Sam Gardner-Dell
 */
@Api(description = "Records Resource",
        tags = { "Records" },
        // Specifying consumes/produces (again) here allows us to default the swagger ui to json
        consumes = APPLICATION_JSON + "," + APPLICATION_XML,
        produces = APPLICATION_JSON + "," + APPLICATION_XML
)
@Path("/datasets/{dataset_id}/records")
@Consumes({ APPLICATION_JSON, APPLICATION_XML })
@Produces({ APPLICATION_JSON, APPLICATION_XML })
@Component
@Scope(ConfigurableBeanFactory.SCOPE_SINGLETON)
public class RecordResource {
    private static final Logger LOGGER = LoggerFactory.getLogger(RecordResource.class);
    private final ApplicationContext context;
    private final RecordAdaptor recordAdaptor;
    private final SubmissionService submissionService;
    private final DatasetService datasetService;

    @Context
    private UriInfo uriInfo;

    /**
     * Create a new {@link RecordResource} RESTful service
     *
     * @param context the spring application context
     */
    @Inject
    public RecordResource(final ApplicationContext context, RecordAdaptor recordAdaptor, DatasetService datasetService, SubmissionService submissionService) {
        this.recordAdaptor = recordAdaptor;
        this.context = context;
        this.datasetService = datasetService;
        this.submissionService = submissionService;
    }

    /**
     * List all records for the given dataset_id
     *
     * @param datasetId the unique identifier for the target dataset
     * @return a response containing an {@link EntityListResponse} entity
     * @throws Exception if the request cannot be completed normally.
     */
    @GET
    @ApiOperation(value = "List records",
            notes = "This operation will list all records for the given `dataset_id`."
    )
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "OK", response = EntityListResponse.class),
            @ApiResponse(
                    code = 404,
                    message = "Not Found - The `dataset_id` parameter did not match a known resource",
                    response = ErrorResponse.class
            )
    })
    public Response listRecords(
            @PathParam("dataset_id") @Pattern(regexp = "[A-Za-z0-9_-]+")
            @ApiParam("The unique identifier for the target dataset") final String datasetId)
            throws Exception {

        DatasetEntity datasetEntity = datasetService.getDataset(datasetId);
        Response.ResponseBuilder rb;
        if (datasetEntity == null) {
            rb = ErrorResponse.DATASET_NOT_FOUND.toResponseBuilder();
        } else {
            List<RecordEntity> records = submissionService.getRecords(datasetEntity);
            List<EntityReference> result = new ArrayList<>();
            for (RecordEntity record : records) {
                result.add(new EntityReference(record.getIdentifier(), Linker.info(uriInfo).record(datasetId, record.getIdentifier())));
            }
            EntityListResponse rw = new EntityListResponse(result);
            rb = rw.toResponseBuilder();
        }
        return rb.build();
    }

    /**
     * Batch record request (create/update)
     *
     * @param datasetId the unique identifier for the target dataset
     * @param batchRequest the batch record request data
     * @return a response containing a {@link MultiStatusResponse} entity
     * @throws Exception if the request cannot be completed normally.
     */
    @POST
    @ApiOperation(value = "Create or update multiple records",
            notes = "Multiple records can be create or updated in a single POST request by passing a collection of individual requests "
                    + "in the request body.\n\n"
                    + "Each request passed within the collection can contain its own set of preconditions as per the conditional request "
                    + "mechanism.\n\n"
                    + "To enable server-side ID generation omit the record_id from the individual request body.\n\n"
                    + "The response body uses a multistatus structure based on the principles outlined in "
                    + "https://tools.ietf.org/html/rfc4918#section-13."
    )
    @ApiResponses(value = {
            @ApiResponse(
                    code = 207,
                    message = "Multi-Status - responses to each create/update request are encoded in the response body",
                    response = MultiStatusResponse.class
            ),
            @ApiResponse(
                    code = 400,
                    message = "Bad Request - no request items could be extracted from the request body.",
                    response = ErrorResponse.class
            ),
            @ApiResponse(code = 404, message = "Not Found - The `dataset_id` parameter did not match a known resource.")
    })
    public Response postRecords(
            @PathParam("dataset_id")
            @Pattern(regexp = "[A-Za-z0-9_-]+")
            @ApiParam("The unique identifier for the target dataset") final String datasetId,
            @ApiParam("The bulk record create/update request") final BatchRecordRequest batchRequest
    )
            throws Exception {

        Response.ResponseBuilder rb;
        DatasetEntity datasetEntity = datasetService.getDataset(datasetId);

        if (datasetEntity == null) {
            rb = ErrorResponse.DATASET_NOT_FOUND.toResponseBuilder();
        } else if (batchRequest.getRequests().isEmpty()) {
            rb = ErrorResponse.MULTISTATUS_REQUEST_EMPTY.toResponseBuilder();
        } else {

            Map<String, RecordEntity> recordEntities = new HashMap<>();
            Map<String, Response.ResponseBuilder> preconditionFailures = new HashMap<>();
            Map<String, Response.Status> responses = new HashMap<>();

            // TODO: Graham, we are hard-coded to DataSamplePayload here, we should use a factory inside the service layer to prevent this

            // Build requests for the service layer
            List<SubmissionService.ObservationIdentifierPair<DataSamplePayload>> submissible = new ArrayList<>();
            for (BatchRecordRequestItem request : batchRequest.getRequests()) {
                RecordEntity recordEntity = submissionService.getRecord(datasetEntity, request.getRecordId());

                // Store default response status
                Response.Status defaultResponse = recordEntity != null ? Response.Status.OK : Response.Status.CREATED;
                responses.put(request.getRecordId(), defaultResponse);

                // Store existing entity
                recordEntities.put(request.getRecordId(), recordEntity);

                // Check preconditions, building a list of submissable entries and any precondition failures
                Response.ResponseBuilder failureResponse = checkPreconditions(datasetId, recordEntity, request.getPreconditions());
                if (failureResponse == null) {
                    submissible.add(new SubmissionService.ObservationIdentifierPair(request.getRecordId(), request.getPayload()));
                } else {
                    preconditionFailures.put(request.getRecordId(), failureResponse);
                }
            }

            // Create and validate records (update the recordEntities map with the latest version)
            recordEntities.putAll(submissionService.createRecords(datasetEntity, submissible)
                    .stream()
                    .collect(Collectors.toMap(RecordEntity::getIdentifier, e -> e)));

            submissionService.validate(recordEntities.values());

            // Build the response
            MultiStatusResponse multiResponse = new MultiStatusResponse();
            for (BatchRecordRequestItem request : batchRequest.getRequests()) {
                MultiStatusResponse.Response responseItem = new MultiStatusResponse.Response();
                responseItem.setId(request.getRecordId());

                RecordEntity recordEntity = recordEntities.get(request.getRecordId());
                Response.ResponseBuilder failureResponse = preconditionFailures.get(request.getRecordId());

                if (failureResponse == null) {
                    // Preconditions passed, service the request
                    Record record = fromEntity(datasetId, recordEntity);

                    responseItem.setCode(responses.get(request.getRecordId()).getStatusCode());
                    responseItem.setHref(Linker.info(uriInfo).record(datasetId, request.getRecordId()));
                    responseItem.setEntityTag(Preconditions.createEtag(record).toString());
                    responseItem.setLastModified(record.getLastModified());
                } else {
                    // Preconditions failed, build a response item from the ResponseBuilder returned by the preconditions checks
                    Response response = failureResponse.build();
                    responseItem.setCode(response.getStatus());

                    if (recordEntity != null) {
                        // Entity does exist so populate the href
                        responseItem.setHref(Linker.info(uriInfo).record(datasetId, request.getRecordId()));
                    }
                }
                multiResponse.addResponse(responseItem);
            }
            rb = multiResponse.toResponseBuilder();
        }
        return rb.build();
    }

    /**
     * Retrieve record data for the given `record_id` and `dataset_id`
     *
     * @param datasetId the unique identifier for the target dataset
     * @param recordId the unique identifier for the target record
     * @param preconditions conditional request structure
     * @return a response containing an {@link RecordEntityResponse} entity
     * @throws Exception if the request cannot be completed normally.
     */
    @GET
    @Path("/{record_id}")
    @ApiOperation(value = "Retrieve record details",
            notes = "Retrieve record data for the given `record_id` and `dataset_id`"
    )
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "OK", response = RecordEntityResponse.class),
            @ApiResponse(code = 304, message = "Not Modified - see conditional request documentation"),
            @ApiResponse(
                    code = 404,
                    message = "Not Found - The `dataset_id` or `record_id` parameter did not match a known resource - "
                            + "see the `meta` structure in the response envelope for more detail"
            )
    })
    public Response getRecord(
            @PathParam("dataset_id")
            @Pattern(regexp = "[A-Za-z0-9_-]+")
            @ApiParam("The unique identifier for the target dataset") final String datasetId,
            @PathParam("record_id")
            @Pattern(regexp = "[A-Za-z0-9_-]+")
            @ApiParam("The unique identifier for a rcord") final String recordId,
            @BeanParam Preconditions preconditions
    )
            throws Exception {

        Response.ResponseBuilder rb;
        DatasetEntity datasetEntity = datasetService.getDataset(datasetId);

        if (datasetEntity == null) {
            rb = ErrorResponse.DATASET_NOT_FOUND.toResponseBuilder();
        } else {
            RecordEntity recordEntity = submissionService.getRecord(datasetEntity, recordId);
            if (recordEntity == null) {
                rb = ErrorResponse.RECORD_NOT_FOUND.toResponseBuilder();
            } else {
                rb = checkPreconditions(datasetId, recordEntity, preconditions);
                if (rb == null) {
                    Record record = fromEntity(datasetId, recordEntity);
                    RecordEntityResponse responseWrapper = new RecordEntityResponse(Response.Status.OK, record);
                    rb = responseWrapper.toResponseBuilder();
                }
            }
        }
        return rb.build();
    }

    /**
     * Create or update the record with the given record_id for the dataset with the given dataset_id
     *
     * @param datasetId the unique identifier for the target dataset
     * @param recordId the unique identifier for the target record
     * @param payload the data payload to associate with the record
     * @param preconditions conditional request structure
     * @return a response containing an {@link RecordEntityResponse} entity
     * @throws Exception if the request cannot be completed normally.
     */
    @PUT
    @Path("/{record_id}")
    @ApiOperation(value = "Create or update record",
            notes = "Create or update the record with the given `record_id` for the dataset with the given `dataset_id`."
    )
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "OK - an existing record was successfully updated.", response = RecordEntityResponse.class),
            @ApiResponse(code = 201, message = "Created - a new record was created.", response = RecordEntityResponse.class),
            @ApiResponse(
                    code = 404,
                    message = "Not Found - The `dataset_id` or `record_id` parameter did not match a known resource - "
                            + "see the `meta` structure in the response envelope for more detail",
                    response = ErrorResponse.class
            ),
            @ApiResponse(
                    code = 412,
                    message = "Precondition Failed - see conditional request documentation",
                    response = ErrorResponse.class
            )
    })
    public Response putRecord(
            @PathParam("dataset_id")
            @Pattern(regexp = "[A-Za-z0-9_-]+")
            @ApiParam("The unique identifier for the target dataset") final String datasetId,

            @PathParam("record_id")
            @Pattern(regexp = "[A-Za-z0-9_-]+")
            @ApiParam("The unique identifier for the target record") final String recordId,
            @ApiParam("The data payload to be associated with the record") final Payload payload,

            @BeanParam Preconditions preconditions
    )
            throws Exception {

        Response.ResponseBuilder rb;
        DatasetEntity datasetEntity = datasetService.getDataset(datasetId);

        if (datasetEntity == null) {
            rb = ErrorResponse.DATASET_NOT_FOUND.toResponseBuilder();
        } else {
            RecordEntity existingEntity = submissionService.getRecord(datasetEntity, recordId);

            Response.Status entityResponseStatus = existingEntity != null ? Response.Status.OK : Response.Status.CREATED;
            rb = checkPreconditions(datasetId, existingEntity, preconditions);
            if (rb == null) {
                // Preconditions passed, create/update the record
                existingEntity = submissionService
                        .createRecord(datasetEntity, new SubmissionService.ObservationIdentifierPair(recordId, payload));
                submissionService.validate(existingEntity);

                Record record = fromEntity(datasetId, existingEntity);
                RecordEntityResponse response = new RecordEntityResponse(entityResponseStatus, record);
                rb = response.toResponseBuilder();
            }
        }
        return rb.build();
    }

    /**
     * Delete the record with the given record_id from the dataset with the given dataset_id
     *
     * @param datasetId the unique identifier for the target dataset
     * @param recordId the unique identifier for the target record
     * @param preconditions conditional request structure
     * @return an empty response body as per HTTP 204
     * @throws Exception if the request cannot be completed normally.
     */
    @DELETE
    @Path("/{record_id}")
    @ApiOperation(value = "Delete record",
            notes = "Delete the record with the given `record_id` from the dataset with the given `dataset_id`"
    )
    @ApiResponses(value = {
            @ApiResponse(code = 204, message = "No Content - an existing record was successfully deleted."),
            @ApiResponse(
                    code = 404,
                    message = "Not Found - The `dataset_id` or `record_id` parameter did not match a known resource - "
                            + "see the `meta` structure in the response envelope for more detail",
                    response = ErrorResponse.class
            ),
            @ApiResponse(
                    code = 412,
                    message = "Precondition Failed - see conditional request documentation",
                    response = ErrorResponse.class
            )
    })
    public Response deleteRecord(
            @PathParam("dataset_id")
            @Pattern(regexp = "[A-Za-z0-9_-]+")
            @ApiParam("The unique identifier for the target dataset") final String datasetId,
            @PathParam("record_id")
            @Pattern(regexp = "[A-Za-z0-9_-]+")
            @ApiParam("The unique identifier for the target record") final String recordId,
            @BeanParam Preconditions preconditions
    )
            throws Exception {

        Response.ResponseBuilder rb;
        DatasetEntity datasetEntity = datasetService.getDataset(datasetId);

        if (datasetEntity == null) {
            rb = ErrorResponse.DATASET_NOT_FOUND.toResponseBuilder();
        } else {
            RecordEntity recordEntity = submissionService.getRecord(datasetEntity, recordId);
            if (recordEntity == null) {
                rb = ErrorResponse.RECORD_NOT_FOUND.toResponseBuilder();
            } else {
                rb = checkPreconditions(datasetId, recordEntity, preconditions);
                if (rb == null) {
                    // Preconditions passed, delete the record
                    submissionService.removeRecord(recordEntity);
                    rb = Response.status(Response.Status.NO_CONTENT);
                }
            }
        }
        return rb.build();
    }

    private Record fromEntity(String datasetId, RecordEntity entity) {
        Record record = recordAdaptor.convert(entity);
        Linker.info(uriInfo).resolve(datasetId, record);
        return record;

    }

    private Response.ResponseBuilder checkPreconditions(final String datasetId, final RecordEntity recordEntity, Preconditions
            preconditions) {
        Response.ResponseBuilder rb = null;
        if (preconditions != null) {
            if (recordEntity == null) {
                rb = preconditions.evaluatePreconditions();
            } else {
                Record existingRecord = fromEntity(datasetId, recordEntity);
                Date lastModified = Date.from(recordEntity.getLastChangedDate());
                rb = preconditions.evaluatePreconditions(lastModified, Preconditions.createEtag(existingRecord));
            }
        }
        return rb;
    }
}