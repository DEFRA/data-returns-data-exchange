package uk.gov.ea.datareturns.web.resource.v1;

import io.swagger.annotations.*;
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.Pair;
import org.apache.commons.lang3.tuple.Triple;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import uk.gov.ea.datareturns.domain.jpa.entities.userdata.AbstractPayloadEntity;
import uk.gov.ea.datareturns.domain.jpa.entities.userdata.impl.DatasetEntity;
import uk.gov.ea.datareturns.domain.jpa.entities.userdata.impl.RecordEntity;
import uk.gov.ea.datareturns.domain.jpa.entities.userdata.impl.User;
import uk.gov.ea.datareturns.domain.jpa.entities.userdata.impl.ValidationError;
import uk.gov.ea.datareturns.domain.jpa.service.DatasetService;
import uk.gov.ea.datareturns.domain.jpa.service.SubmissionService;
import uk.gov.ea.datareturns.util.StopWatch;
import uk.gov.ea.datareturns.web.resource.v1.model.common.Linker;
import uk.gov.ea.datareturns.web.resource.v1.model.common.Preconditions;
import uk.gov.ea.datareturns.web.resource.v1.model.common.references.EntityReference;
import uk.gov.ea.datareturns.web.resource.v1.model.dataset.*;
import uk.gov.ea.datareturns.web.resource.v1.model.record.Record;
import uk.gov.ea.datareturns.web.resource.v1.model.record.RecordAdaptor;
import uk.gov.ea.datareturns.web.resource.v1.model.record.payload.Payload;
import uk.gov.ea.datareturns.web.resource.v1.model.request.BatchDatasetRequest;
import uk.gov.ea.datareturns.web.resource.v1.model.request.BatchDatasetRequestItem;
import uk.gov.ea.datareturns.web.resource.v1.model.response.*;

import javax.inject.Inject;
import javax.validation.constraints.Pattern;
import javax.ws.rs.*;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Collectors;

import static javax.ws.rs.core.MediaType.APPLICATION_JSON;
import static javax.ws.rs.core.MediaType.APPLICATION_XML;

/**
 * RESTful resource to manage dataset entities.
 *
 * @author Sam Gardner-Dell
 */
@Api(description = "Dataset Resource",
        tags = { "Datasets" },
        // Specifying consumes/produces (again) here allows us to default the swagger ui to json
        consumes = APPLICATION_JSON + "," + APPLICATION_XML,
        produces = APPLICATION_JSON + "," + APPLICATION_XML
)
@Path("/datasets")
@Consumes({ APPLICATION_JSON, APPLICATION_XML })
@Produces({ APPLICATION_JSON, APPLICATION_XML })
@Component
@Scope(ConfigurableBeanFactory.SCOPE_SINGLETON)
public class DatasetResource {
    private static final Logger LOGGER = LoggerFactory.getLogger(DatasetResource.class);

    @Context
    private UriInfo uriInfo;

    @Inject
    private SubmissionService submissionService;

    @Inject
    private DatasetService datasetService;

    @Inject
    RecordAdaptor recordAdaptor;

    /**
     * List the available datasets
     *
     * @param preconditions conditional request structure
     * @return a response containing an {@link EntityListResponse} entity
     * @throws Exception if the request cannot be completed normally.
     */
    @GET
    @ApiOperation(value = "List the available datasets",
            notes = "This operation will list all datasets previously created by the API consumer.  Multiple API consumers (using "
                    + "different authentication credentials) may use datasets with the same `dataset_id` and these will operate "
                    + "independently of each other."
    )
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "OK", response = EntityListResponse.class),
            @ApiResponse(code = 304, message = "Not Modified - see conditional request documentation")
    })
    public Response listDatasets(@BeanParam Preconditions preconditions) throws Exception {
        User user = datasetService.getSystemUser();
        List<DatasetEntity> datasets = datasetService.getDatasets(user);

        return onPreconditionsPass(user, datasets, preconditions,
                () -> {
                    List<EntityReference> entityReferences = datasets.stream()
                            .map(e -> new EntityReference(e.getIdentifier(), Linker.info(uriInfo).dataset(e.getIdentifier())))
                            .collect(Collectors.toList());
                    return new EntityListResponse(entityReferences,
                            Date.from(user.getDatasetChangedDate()),
                            Preconditions.createEtag(datasets)
                    ).toResponseBuilder();
                }).build();
    }

    /**
     * Batch dataset request (create/update)
     *
     * @param batchRequest the batch dataset request data
     * @return a response containing a {@link MultiStatusResponse} entity
     * @throws Exception if the request cannot be completed normally.
     */
    @POST
    @ApiOperation(value = "Create or update multiple datasets",
            notes = "Multiple datasets can be create or updated in a single POST request by passing a collection of individual requests "
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
            )
    })
    public Response postDatasets(
            @ApiParam("The bulk dataset create/update request") final BatchDatasetRequest batchRequest
    )
            throws Exception {

        Response.ResponseBuilder rb;

        if (batchRequest.getRequests().isEmpty()) {
            rb = ErrorResponse.MULTISTATUS_REQUEST_EMPTY.toResponseBuilder();
        } else {
            MultiStatusResponse multiResponse = new MultiStatusResponse();

            for (BatchDatasetRequestItem request : batchRequest.getRequests()) {
                DatasetEntity datasetEntity = datasetService.getDataset(request.getDatasetId());
                MultiStatusResponse.Response responseItem = new MultiStatusResponse.Response();
                responseItem.setId(request.getDatasetId());

                Response.ResponseBuilder failureResponse = onPreconditionsPass(datasetEntity, request.getPreconditions(), () -> {
                    // Preconditions passed, service the request
                    DatasetEntityResponse storeResult = storeDataset(datasetEntity, request.getDatasetId(), request.getProperties());
                    Dataset dataset = storeResult.getData();

                    responseItem.setCode(storeResult.getMeta().getStatus());
                    responseItem.setHref(Linker.info(uriInfo).dataset(dataset.getId()));
                    responseItem.setEntityTag(Preconditions.createEtag(dataset).toString());
                    responseItem.setLastModified(dataset.getLastModified());
                    return null;
                });
                if (failureResponse != null) {
                    // Preconditions failed, build a response item from the ResponseBuilder returned by the preconditions checks
                    Response response = failureResponse.build();
                    responseItem.setCode(response.getStatus());

                    if (datasetEntity != null) {
                        // Entity does exist so populate the href
                        responseItem.setHref(Linker.info(uriInfo).dataset(request.getDatasetId()));
                    }
                }
                multiResponse.addResponse(responseItem);
            }
            rb = multiResponse.toResponseBuilder();
        }
        return rb.build();
    }

    /**
     * Retrieve dataset details
     *
     * @param datasetId the unique identifier for the target dataset
     * @param preconditions conditional request structure
     * @return a response containing an {@link DatasetEntityResponse} entity
     * @throws Exception if the request cannot be completed normally.
     */
    @GET
    @Path("/{dataset_id}")
    @ApiOperation(value = "Retrieve dataset details",
            notes = "**Retrieve the details for the given `dataset_id`**"
    )
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "OK", response = DatasetEntityResponse.class),
            @ApiResponse(code = 304, message = "Not Modified - see conditional request documentation"),
            @ApiResponse(
                    code = 404,
                    message = "Not Found - The `dataset_id` parameter did not match a known resource",
                    response = ErrorResponse.class
            )
    })
    public Response getDataset(
            @PathParam("dataset_id")
            @Pattern(regexp = "[A-Za-z0-9_-]+")
            @ApiParam("The unique identifier for the target dataset") final String datasetId,
            @BeanParam Preconditions preconditions)
            throws Exception {
        return onDataset(datasetId, datasetEntity ->
                onPreconditionsPass(datasetEntity, preconditions, () -> {
                    Dataset dataset = fromEntity(datasetEntity);
                    return new DatasetEntityResponse(Response.Status.OK, dataset).toResponseBuilder();
                })
        ).build();
    }

    /**
     * Create or update the properties associated with the dataset for the specified dataset_id
     *
     * @param datasetId the unique identifier for the target dataset
     * @param preconditions conditional request structure
     * @param datasetProperties user-defineable properties to associate with the DatasetEntity
     * @return a response containing an {@link DatasetEntityResponse} entity
     * @throws Exception if the request cannot be completed normally.
     */
    @PUT
    @Path("/{dataset_id}")
    @ApiOperation(value = "Create or update a dataset",
            notes = "**Create or update the properties associated with the dataset for the specified `dataset_id`**\n"
                    + "#### Note\n"
                    + "* This operation does not effect the records stored within an existing dataset."
    )
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "OK - an existing dataset was successfully updated.", response = DatasetEntityResponse.class),
            @ApiResponse(code = 201, message = "Created - a new dataset was created and can now accept records.", response = DatasetEntityResponse.class),
            @ApiResponse(
                    code = 412,
                    message = "Precondition Failed - see conditional request documentation",
                    response = ErrorResponse.class
            )
    })
    public Response putDataset(
            @PathParam("dataset_id")
            @Pattern(regexp = "[A-Za-z0-9_-]+")
            @ApiParam("The unique identifier for the target dataset") final String datasetId,
            @BeanParam Preconditions preconditions,
            final DatasetProperties datasetProperties
    )
            throws Exception {
        DatasetEntity datasetEntity = datasetService.getDataset(datasetId);
        return onPreconditionsPass(datasetEntity, preconditions, () -> {
            // Preconditions passed, process request
            return storeDataset(datasetEntity, datasetId, datasetProperties).toResponseBuilder();
        }).build();
    }

    /**
     * Delete the dataset and any records currently associated with the specified dataset_id

     * @param datasetId the unique identifier for the target dataset
     * @param preconditions conditional request structure
     * @return an empty response body as per HTTP 204
     * @throws Exception if the request cannot be completed normally.
     */
    @DELETE
    @Path("/{dataset_id}")
    @ApiOperation(value = "Delete a dataset",
            notes = "**Delete the dataset and any records currently associated with the specified `dataset_id`**"
    )
    @ApiResponses(value = {
            @ApiResponse(code = 204, message = "No Content - an existing dataset was successfully deleted."),
            @ApiResponse(
                    code = 404,
                    message = "Not Found - The `dataset_id` parameter did not match a known resource",
                    response = ErrorResponse.class
            ),
            @ApiResponse(
                    code = 412,
                    message = "Precondition Failed - see conditional request documentation",
                    response = ErrorResponse.class
            )
    })
    public Response deleteDataset(
            @PathParam("dataset_id")
            @Pattern(regexp = "[A-Za-z0-9_-]+")
            @ApiParam("The unique identifier for the target dataset") final String datasetId,
            @BeanParam Preconditions preconditions)
            throws Exception {
        return onDataset(datasetId, datasetEntity ->
                onPreconditionsPass(datasetEntity, preconditions, () -> {
                    // Preconditions passed, delete the resource
                    datasetService.removeDataset(datasetId);
                    return Response.status(Response.Status.NO_CONTENT);
                })
        ).build();
    }

    /**
     * Retrieve the dataset status information for the specified dataset_id
     *
     * @param datasetId the unique identifier for the target dataset
     * @return a response containing an {@link DatasetStatusResponse} entity
     * @throws Exception if the request cannot be completed normally.
     */
    @GET
    @Path("/{dataset_id}/status")
    @ApiOperation(value = "Retrieve dataset status",
            notes = "**Retrieve the dataset status information for the specified `dataset_id`**\n\n"
                    + "Status information includes details such as any validation errors and the submission status"
    )
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "OK", response = DatasetStatusResponse.class),
            @ApiResponse(
                    code = 404,
                    message = "Not Found - The `dataset_id` parameter did not match a known resource",
                    response = ErrorResponse.class
            )
    })
    public Response getDatasetStatus(
            @PathParam("dataset_id")
            @Pattern(regexp = "[A-Za-z0-9_-]+")
            @ApiParam("The unique identifier for the target dataset") final String datasetId)
            throws Exception {
        return onDataset(datasetId, datasetEntity ->
                new DatasetStatusResponse(buildDatasetStatus(datasetEntity)).toResponseBuilder()
        ).build();
    }

    /**
     * Attempt to set dataset status information
     *
     * @param datasetId the unique identifier for the target dataset
     * @param requestedStatus a {@link DatasetSubmissionStatus} object containing the desired target status
     * @return a response containing an {@link DatasetStatusResponse} entity
     * @throws Exception if the request cannot be completed normally.
     */
    @PUT
    @Path("/{dataset_id}/status")
    @ApiOperation(value = "Set dataset status",
            notes = "**Set the dataset status information for the specified `dataset_id`**\n\n"
                    + "- can be used to submit a dataset to the environment agency"
    )
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "OK", response = DatasetStatusResponse.class),
            @ApiResponse(
                    code = 400,
                    message = "Bad Request - Either the new status was invalid or the state transition was not allowed.",
                    response = ErrorResponse.class
            ),
            @ApiResponse(
                    code = 404,
                    message = "Not Found - The `dataset_id` parameter did not match a known resource",
                    response = ErrorResponse.class
            ),
            @ApiResponse(
                    code = 409,
                    message = "Conflict - The dataset is not currently submittable.  Either there are validation errors or required "
                            + "information is missing.",
                    response = ErrorResponse.class
            )
    })
    public Response setDatasetStatus(
            @PathParam("dataset_id")
            @Pattern(regexp = "[A-Za-z0-9_-]+")
            @ApiParam("The unique identifier for the target dataset") final String datasetId,
            @ApiParam("The requested status.") final DatasetSubmissionStatus requestedStatus)
            throws Exception {
        return onDataset(datasetId, datasetEntity -> {
            // Check state transition
            DatasetSubmissionStatus submissionStatus = getSubmissionStatus(datasetEntity);
            if (!submissionStatus.getStatus().canTransition(requestedStatus.getStatus())) {
                return ErrorResponse.SUBMISSION_INVALID_STATE_CHANGE.toResponseBuilder();
            }
            if (datasetEntity.getOriginatorEmail() == null) {
                return ErrorResponse.UNSUBMITTABLE_BAD_ORIGINATOR.toResponseBuilder();
            }
            if (!submissionService.getInvalidRecords(datasetEntity).isEmpty()) {
                return ErrorResponse.UNSUBMITTABLE_VALIDATION_ERRORS.toResponseBuilder();
            }

            // Submit the data - only valid records may be submitted
            submissionService.submit(submissionService.getRecords(datasetEntity));

            return new DatasetStatusResponse(buildDatasetStatus(datasetEntity)).toResponseBuilder();
        }).build();
    }

    /**
     * Persist or update a dataset
     * @param datasetId
     * @param datasetProperties
     * @return DatasetEntityResponse
     */
    private DatasetEntityResponse storeDataset(final DatasetEntity datasetEntity, String datasetId,
            final DatasetProperties datasetProperties) {
        Dataset dataset;
        DatasetEntity newDatasetEntity;
        Response.Status status = Response.Status.OK;
        if (datasetEntity != null) {
            dataset = DatasetAdaptor.getInstance().convert(datasetEntity);
            dataset.setProperties(datasetProperties);
            newDatasetEntity = DatasetAdaptor.getInstance().merge(datasetEntity, dataset);
            datasetService.updateDataset(newDatasetEntity);
        } else {
            dataset = new Dataset();
            dataset.setId(datasetId);
            dataset.setProperties(datasetProperties);
            newDatasetEntity = DatasetAdaptor.getInstance().merge(datasetEntity, dataset);
            datasetService.createDataset(newDatasetEntity);
            status = Response.Status.CREATED;
        }
        dataset = fromEntity(newDatasetEntity);
        return new DatasetEntityResponse(status, dataset);
    }

    private DatasetStatus buildDatasetStatus(final DatasetEntity datasetEntity) {
        DatasetStatus datasetStatus = new DatasetStatus();
        datasetStatus.setSubmission(getSubmissionStatus(datasetEntity));
        datasetStatus.setSubstitutions(getSubstitutions(datasetEntity));
        datasetStatus.setValidity(getValidity(datasetEntity));
        return datasetStatus;
    }

    private DatasetSubstitutions getSubstitutions(final DatasetEntity datasetEntity) {
        StopWatch sw = new StopWatch("substitutions");
        sw.startTask("Retrieving substitutions");

        List<RecordEntity> recordEntities = submissionService.getRecords(datasetEntity);
        DatasetSubstitutions substitutions = new DatasetSubstitutions();
        recordEntities = submissionService.evaluateSubstitutes(recordEntities);
        for (RecordEntity recordEntity : recordEntities) {
            if (recordEntity.getAbstractPayloadEntity() != null) {
                for (AbstractPayloadEntity.EntitySubstitution entitySubstitution : recordEntity.getAbstractPayloadEntity()
                        .getEntitySubstitutions()) {
                    substitutions.addSubstitution(recordEntity.getIdentifier(),
                            entitySubstitution.getEntity(),
                            entitySubstitution.getSubmitted(),
                            entitySubstitution.getPreferred());
                }
            }
        }

        sw.stopTask();
        LOGGER.info(sw.prettyPrint());

        return substitutions;
    }

    private DatasetSubmissionStatus getSubmissionStatus(final DatasetEntity datasetEntity) {
        DatasetSubmissionStatus submissionStatus = new DatasetSubmissionStatus();

        if (datasetEntity.getStatus() == DatasetEntity.Status.SUBMITTED) {
            submissionStatus.setStatus(DatasetSubmissionStatus.Status.SUBMITTED);
        } else {
            submissionStatus.setStatus(DatasetSubmissionStatus.Status.UNSUBMITTED);
        }

        return submissionStatus;
    }

    private DatasetValidity getValidity(final DatasetEntity datasetEntity) {
        StopWatch sw = new StopWatch("validity");
        sw.stopTask();
        sw.startTask("Getting record errors");

        // Retrieve the list of tuples containing the
        // record identifier, the payload type and the error
        List<Triple<String, String, String>> validationErrors
                = submissionService.retrieveValidationErrors(datasetEntity);

        Map<Pair<String, String>, List<String>> groupedValidationErrors = validationErrors
                .stream()
                .collect(Collectors.groupingBy(k -> new ImmutablePair(k.getLeft(), k.getMiddle()),
                        Collectors.mapping(Triple::getRight, Collectors.toList())));

        Linker linker = Linker.info(uriInfo);
        DatasetValidity validity = new DatasetValidity();

        if (!groupedValidationErrors.isEmpty()) {

            // Iterate over the records
            for (Pair<String, String> pair : groupedValidationErrors.keySet()) {
                // Extract the three fields
                String recordId = pair.getLeft();
                String payloadType = pair.getRight();
                List<String> errors = groupedValidationErrors.get(pair);

                EntityReference recordRef = new EntityReference(recordId,
                        linker.record(datasetEntity.getIdentifier(), recordId));

                for (String recordValidationError : errors) {
                    EntityReference validationRef = new EntityReference(recordValidationError,
                            linker.constraint(payloadType, recordValidationError));

                    validity.addViolation(validationRef, recordRef);
                }
            }
        }

        sw.stopTask();
        LOGGER.info(sw.prettyPrint());

        return validity;
    }

    private Dataset fromEntity(DatasetEntity entity) {
        Dataset dataset = DatasetAdaptor.getInstance().convert(entity);
        Linker.info(uriInfo).resolve(dataset);
        return dataset;
    }

    private Response.ResponseBuilder onDataset(String datasetId, Function<DatasetEntity, Response.ResponseBuilder> handler) {
        DatasetEntity datasetEntity = datasetService.getDataset(datasetId);
        return (datasetEntity == null) ? ErrorResponse.DATASET_NOT_FOUND.toResponseBuilder() : handler.apply(datasetEntity);
    }

    private Response.ResponseBuilder onPreconditionsPass(final DatasetEntity datasetEntity, Preconditions preconditions, Supplier<Response
            .ResponseBuilder> handler) {
        Response.ResponseBuilder rb = null;
        if (preconditions != null) {
            if (datasetEntity == null) {
                rb = preconditions.evaluatePreconditions();
            } else {
                Dataset existingDataset = fromEntity(datasetEntity);
                Date lastModified = Date.from(datasetEntity.getLastChangedDate());
                rb = preconditions.evaluatePreconditions(lastModified, Preconditions.createEtag(existingDataset));
            }
        }
        if (rb == null) {
            rb = handler.get();
        }
        return rb;
    }

    // Precondition evaluator for the entity list held at user level
    private Response.ResponseBuilder onPreconditionsPass(final User user, List<DatasetEntity> datasets,
            Preconditions preconditions, Supplier<Response.ResponseBuilder> handler) {

        Response.ResponseBuilder rb = null;
        if (preconditions != null) {
            if (user == null || datasets == null) {
                rb = preconditions.evaluatePreconditions();
            } else {
                Date lastModified = Date.from(user.getDatasetChangedDate());
                rb = preconditions.evaluatePreconditions(lastModified, Preconditions.createEtag(datasets));
            }
        }
        if (rb == null) {
            rb = handler.get();
        }
        return rb;
    }
}