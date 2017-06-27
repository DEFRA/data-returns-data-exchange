package uk.gov.ea.datareturns.web.resource.v1;

import io.swagger.annotations.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import uk.gov.ea.datareturns.domain.jpa.entities.userdata.impl.DatasetEntity;
import uk.gov.ea.datareturns.domain.jpa.entities.userdata.impl.RecordEntity;
import uk.gov.ea.datareturns.domain.jpa.service.DatasetService;
import uk.gov.ea.datareturns.domain.jpa.service.SubmissionService;
import uk.gov.ea.datareturns.util.StopWatch;
import uk.gov.ea.datareturns.web.resource.v1.model.common.Linker;
import uk.gov.ea.datareturns.web.resource.v1.model.common.Preconditions;
import uk.gov.ea.datareturns.web.resource.v1.model.common.references.EntityReference;
import uk.gov.ea.datareturns.web.resource.v1.model.record.Record;
import uk.gov.ea.datareturns.web.resource.v1.model.record.RecordAdaptor;
import uk.gov.ea.datareturns.web.resource.v1.model.record.payload.Payload;
import uk.gov.ea.datareturns.web.resource.v1.model.request.BatchRecordRequest;
import uk.gov.ea.datareturns.web.resource.v1.model.request.BatchRecordRequestItem;
import uk.gov.ea.datareturns.web.resource.v1.model.response.*;

import javax.inject.Inject;
import javax.validation.constraints.Pattern;
import javax.ws.rs.*;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.function.Supplier;
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
    @Context
    private UriInfo uriInfo;

    private SubmissionService submissionService;
    private DatasetService datasetService;

    /**
     * Create a new {@link RecordResource} RESTful service
     *
     * @param context the spring application context
     */
    @Inject
    public RecordResource(final ApplicationContext context, RecordAdaptor recordAdaptor,
            SubmissionService submissionService, DatasetService datasetService) {

        this.recordAdaptor = recordAdaptor;
        this.context = context;
        this.submissionService = submissionService;
        this.datasetService = datasetService;
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
            @ApiResponse(code = 304, message = "Not Modified - see conditional request documentation"),
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
        return onDataset(datasetId, datasetEntity -> {
            List<RecordEntity> records = submissionService.getRecords(datasetEntity);
            return new EntityListResponse(
                    records.stream().map((entity) -> {
                        String uri = Linker.info(uriInfo).record(datasetId, entity.getIdentifier());
                        return new EntityReference(entity.getIdentifier(), uri);
                    }).collect(Collectors.toList()),
                    Date.from(datasetEntity.getRecordChangedDate()),
                    Preconditions.createEtag(records)
            ).toResponseBuilder();
        }).build();
    }

    /**
     * Retrieve all record and payload data for the given dataset_id
     *
     * @param datasetId the unique identifier for the target dataset
     * @return a response containing an {@link EntityDataListResponse} entity
     * @throws Exception if the request cannot be completed normally.
     */
    @GET
    @Path("/$data")
    @ApiOperation(value = "List full record data",
            notes = "Retrieve all record and payload data for the given `dataset_id`."
    )
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "OK", response = EntityDataListResponse.class),
            @ApiResponse(code = 304, message = "Not Modified - see conditional request documentation"),
            @ApiResponse(
                    code = 404,
                    message = "Not Found - The `dataset_id` parameter did not match a known resource",
                    response = ErrorResponse.class
            )
    })
    public Response listRecordData(
            @PathParam("dataset_id") @Pattern(regexp = "[A-Za-z0-9_-]+")
            @ApiParam("The unique identifier for the target dataset") final String datasetId)
            throws Exception {
        return onDataset(datasetId, datasetEntity -> {
            List<RecordEntity> records = submissionService.getRecords(datasetEntity);
            return new EntityDataListResponse(
                    records.stream().map(e -> fromEntity(datasetId, e)).collect(Collectors.toList()),
                    Date.from(datasetEntity.getRecordChangedDate()),
                    Preconditions.createEtag(records)
            ).toResponseBuilder();
        }).build();
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
            @ApiResponse(code = 404, message = "Not Found - The `dataset_id` parameter did not match a known resource."),
            @ApiResponse(
                    code = 403,
                    message = "Forbidden - the dataset is already submitted and cannot be ammended",
                    response = ErrorResponse.class
            )
    })
    public Response postRecords(
            @PathParam("dataset_id")
            @Pattern(regexp = "[A-Za-z0-9_-]+")
            @ApiParam("The unique identifier for the target dataset") final String datasetId,
            @ApiParam("The bulk record create/update request") final BatchRecordRequest batchRequest
    )
            throws Exception {

        final StopWatch sw = new StopWatch("postRecords");

        sw.startTask("Retrieving dataset");
        return onDataset(datasetId, datasetEntity -> {
            sw.stopTask();
            LOGGER.info("Fetched dataset: " + sw.getLastTaskTimeMillis());
            sw.startTask("Pre-processing batch");
            Response.ResponseBuilder rb;
            if (batchRequest.getRequests().isEmpty()) {
                rb = ErrorResponse.MULTISTATUS_REQUEST_EMPTY.toResponseBuilder();
            } else {
                List<String> recordIds = batchRequest.getRequests().stream().map(BatchRecordRequestItem::getRecordId)
                        .collect(Collectors.toList());
                Map<String, RecordEntity> recordEntities = submissionService.getRecords(datasetEntity, recordIds);
                Map<String, Response.ResponseBuilder> preconditionFailures = new HashMap<>();
                Map<String, Response.Status> responses = new HashMap<>();
                Map<String, Payload> payloads = new HashMap<>();

                // Build requests for the service layer
                for (BatchRecordRequestItem request : batchRequest.getRequests()) {
                    RecordEntity recordEntity = recordEntities.get(request.getRecordId());
                    Response.Status defaultResponse;

                    if (recordEntity == null) {
                        defaultResponse = Response.Status.CREATED;
                    } else if (datasetEntity.getStatus() == DatasetEntity.Status.SUBMITTED) {
                        defaultResponse = Response.Status.FORBIDDEN;
                    } else {
                        defaultResponse = Response.Status.OK;
                    }

                    // Store default response status
                    responses.put(request.getRecordId(), defaultResponse);

                    // Store existing entity
                    recordEntities.put(request.getRecordId(), recordEntity);

                    // Check preconditions, building a list of entries and any precondition failures
                    Response.ResponseBuilder failureResponse = onPreconditionsPass(datasetId, recordEntity, request.getPreconditions(),
                            () -> {
                                // Preconditions passed, add a new PayloadIdentifier
                                payloads.put(request.getRecordId(), request.getPayload());
                                return null;
                            });

                    if (failureResponse != null) {
                        preconditionFailures.put(request.getRecordId(), failureResponse);
                    }
                }

                sw.stopTask();
                LOGGER.info("Finished preprocessing: " + sw.getLastTaskTimeMillis());
                sw.startTask("Merging records");

                // Create and validate records if they are not already submitted
                recordEntities.putAll(submissionService.createRecords(datasetEntity, payloads));

                sw.stopTask();
                LOGGER.info("Fetched validating: " + sw.getLastTaskTimeMillis());
                sw.startTask("Building response");

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

                sw.stopTask();
                LOGGER.info(sw.prettyPrint());
            }
            return rb;
        }).build();
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
    @Path("/{record_id : [a-zA-Z0-9_-]+ }")
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
        return onDataset(datasetId, (datasetEntity) ->
                onRecord(datasetEntity, recordId, (recordEntity) ->
                        onPreconditionsPass(datasetId, recordEntity, preconditions, () ->
                                new RecordEntityResponse(Response.Status.OK, fromEntity(datasetId, recordEntity)).toResponseBuilder()
                        )
                )
        ).build();
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
            ),
            @ApiResponse(
                    code = 403,
                    message = "Forbidden - the dataset is already submitted and cannot be ammended",
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
        return onDataset(datasetId, (datasetEntity) -> {
            RecordEntity existingEntity = submissionService.getRecord(datasetEntity, recordId);
            return onPreconditionsPass(datasetId, existingEntity, preconditions, () -> {
                Response.Status status = Response.Status.OK;
                Record record = null;

                // If it already exists then we have a conflict
                if (existingEntity == null) {
                    status = Response.Status.CREATED;
                } else if (datasetEntity.getStatus() == DatasetEntity.Status.SUBMITTED) {
                    status = Response.Status.FORBIDDEN;
                    record = fromEntity(datasetId, existingEntity);
                }

                // Preconditions passed, create/update the record
                if (status != Response.Status.FORBIDDEN) {
                    RecordEntity recordEntity = submissionService.createRecord(datasetEntity, recordId, payload);

                    record = fromEntity(datasetId, recordEntity);
                }

                return new RecordEntityResponse(status, record).toResponseBuilder();
            });
        }).build();
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
        return onDataset(datasetId, (datasetEntity) ->
                onRecord(datasetEntity, recordId, (recordEntity) ->
                        onPreconditionsPass(datasetId, recordEntity, preconditions, () -> {
                            // Preconditions passed, delete the record
                            submissionService.removeRecord(datasetEntity, recordEntity.getIdentifier());
                            return Response.status(Response.Status.NO_CONTENT);
                        })
                )
        ).build();
    }

    private Record fromEntity(String datasetId, RecordEntity entity) {
        Record record = recordAdaptor.convert(entity);
        Linker.info(uriInfo).resolve(datasetId, record);
        return record;
    }

    private Response.ResponseBuilder onDataset(String datasetId, Function<DatasetEntity, Response.ResponseBuilder> handler) {
        DatasetEntity datasetEntity = datasetService.getDataset(datasetId);
        return (datasetEntity == null) ? ErrorResponse.DATASET_NOT_FOUND.toResponseBuilder() : handler.apply(datasetEntity);
    }

    private Response.ResponseBuilder onRecord(DatasetEntity datasetEntity, String recordId, Function<RecordEntity, Response
            .ResponseBuilder> handler) {
        RecordEntity recordEntity = submissionService.getRecord(datasetEntity, recordId);
        return (recordEntity == null) ? ErrorResponse.RECORD_NOT_FOUND.toResponseBuilder() : handler.apply(recordEntity);
    }

    private Response.ResponseBuilder onPreconditionsPass(final String datasetId, final RecordEntity recordEntity, Preconditions
            preconditions, Supplier<Response
            .ResponseBuilder> handler) {
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
        if (rb == null) {
            rb = handler.get();
        }
        return rb;
    }
}