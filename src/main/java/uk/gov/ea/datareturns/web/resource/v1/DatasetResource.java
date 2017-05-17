package uk.gov.ea.datareturns.web.resource.v1;

import io.swagger.annotations.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import uk.gov.ea.datareturns.config.SubmissionConfiguration;
import uk.gov.ea.datareturns.domain.jpa.entities.userdata.impl.DatasetEntity;
import uk.gov.ea.datareturns.domain.jpa.service.SubmissionService;
import uk.gov.ea.datareturns.web.resource.v1.model.common.Preconditions;
import uk.gov.ea.datareturns.web.resource.v1.model.common.references.EntityReference;
import uk.gov.ea.datareturns.web.resource.v1.model.dataset.*;
import uk.gov.ea.datareturns.web.resource.v1.model.request.BatchDatasetRequest;
import uk.gov.ea.datareturns.web.resource.v1.model.request.BatchDatasetRequestItem;
import uk.gov.ea.datareturns.web.resource.v1.model.responses.ErrorResponse;
import uk.gov.ea.datareturns.web.resource.v1.model.responses.dataset.DatasetEntityResponse;
import uk.gov.ea.datareturns.web.resource.v1.model.responses.dataset.DatasetListResponse;
import uk.gov.ea.datareturns.web.resource.v1.model.responses.multistatus.MultiStatusResponse;

import javax.annotation.Resource;
import javax.validation.constraints.Pattern;
import javax.ws.rs.*;
import javax.ws.rs.core.*;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import static javax.ws.rs.core.MediaType.APPLICATION_JSON;
import static javax.ws.rs.core.MediaType.APPLICATION_XML;

/**
 * RESTful resource to manage dataset entities.
 *
 * @author Sam Gardner-Dell
 */
@Api(description = "DatasetEntity Resource",
        tags = { "DatasetEntity" },
        // Specifying consumes/produces (again) here allows us to default the swagger ui to json
        consumes = APPLICATION_JSON + "," + APPLICATION_XML,
        produces = APPLICATION_JSON + "," + APPLICATION_XML
)
@Path("/datasets")
@Consumes({ APPLICATION_XML, APPLICATION_JSON })
@Produces({ APPLICATION_XML, APPLICATION_JSON })
@Component
@Scope(ConfigurableBeanFactory.SCOPE_SINGLETON)
public class DatasetResource {
    private static final Logger LOGGER = LoggerFactory.getLogger(DatasetResource.class);

    @Context
    private UriInfo uriInfo;

    private SubmissionService submissionService;

    /**
     * Retrieves the appropriate versioned submission service
     * @param submissionServiceMap
     */
    @Resource(name = "submissionServiceMap")
    private void setSubmissionService(Map<SubmissionConfiguration.SubmissionServiceProvider, SubmissionService> submissionServiceMap) {
        this.submissionService = submissionServiceMap.get(SubmissionConfiguration.SubmissionServiceProvider.LANDFILL_VERSION_1);
    }

    /**
     * List the available datasets
     *
     * @return a response containing an {@link DatasetListResponse} entity
     * @throws Exception if the request cannot be completed normally.
     */
    @GET
    @ApiOperation(value = "List the available datasets",
            notes = "This operation will list all datasets previously created by the API consumer.  Multiple API consumers (using "
                    + "different authentication credentials) may use datasets with the same `dataset_id` and these will operate "
                    + "independently of each other.",
            response = DatasetListResponse.class
    )
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "OK", response = DatasetListResponse.class)
    })
    public Response listDatasets() throws Exception {

        List<EntityReference> result = new ArrayList<>();
        List<DatasetEntity> datasets = submissionService.getDatasets();

        for (DatasetEntity dataset : datasets) {
            UriBuilder ub = uriInfo.getAbsolutePathBuilder();
            result.add(new EntityReference(dataset.getIdentifier(), ub.path(dataset.getIdentifier()).build().toASCIIString()));
        }
        Response.Status responseStatus = Response.Status.OK;
        DatasetListResponse rw = new DatasetListResponse(responseStatus, result);
        return Response.status(responseStatus).entity(rw).build();
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
            )
    })
    public Response postDatasets(
            @ApiParam("The bulk dataset create/update request") final BatchDatasetRequest batchRequest
    )
            throws Exception {

        MultiStatusResponse multiResponse = new MultiStatusResponse();
        for (BatchDatasetRequestItem request : batchRequest.getRequests()) {
            MultiStatusResponse.Response response = new MultiStatusResponse.Response();
            DatasetEntity datasetEntity = submissionService.getDataset(request.getDatasetId());
            Response.ResponseBuilder rb = checkStoragePreconditions(datasetEntity, request.getPreconditions());
            if (rb == null) {
                DatasetEntityResponse responseEntity = storeDataset(datasetEntity, request.getDatasetId(), request.getProperties());
                UriBuilder ub = uriInfo.getAbsolutePathBuilder();
                response.setId(request.getDatasetId());
                response.setCode(responseEntity.getMeta().getStatus());
                response.setHref(ub.path(responseEntity.getData().getId()).build().toASCIIString());
            } else {
                response.setId(request.getDatasetId());
                response.setCode(rb.build().getStatus());
            }
            multiResponse.addResponse(response);
        }
        return Response.status(207).entity(multiResponse).build();
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
            @ApiResponse(code = 404, message = "Not Found - The `dataset_id` parameter did not match a known resource",
                    response = ErrorResponse.class)
    })
    public Response getDataset(
            @PathParam("dataset_id")
            @Pattern(regexp = "[A-Za-z0-9_-]+")
            @ApiParam("The unique identifier for the target dataset") final String datasetId,
            @BeanParam Preconditions preconditions)
            throws Exception {

        Response.ResponseBuilder rb;

        DatasetEntity dataSetEntity = submissionService.getDataset(datasetId);
        Dataset dataset = DatasetAdaptor.getInstance().convert(dataSetEntity);

        if (dataset == null) {
            rb = Response.status(Response.Status.NOT_FOUND);
        } else {
            EntityTag eTag = Preconditions.createEtag(dataset);
            // TODO: Support last modified
            rb = preconditions.evaluatePreconditions(new Date(), eTag);
            if (rb == null) {
                // Preconditions passed, serve resource
                DatasetEntityResponse responseWrapper = new DatasetEntityResponse(Response.Status.OK, dataset);
                rb = Response.status(responseWrapper.getMeta().getStatus())
                        .entity(responseWrapper)
                        .tag(eTag);
            }
        }
        return rb.build();
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
            @ApiResponse(code = 412, message = "Precondition Failed - see conditional request documentation")
    })
    public Response putDataset(
            @PathParam("dataset_id")
            @Pattern(regexp = "[A-Za-z0-9_-]+")
            @ApiParam("The unique identifier for the target dataset") final String datasetId,
            @BeanParam Preconditions preconditions,
            final DatasetProperties datasetProperties
    )
            throws Exception {

        DatasetEntity datasetEntity = submissionService.getDataset(datasetId);

        Response.ResponseBuilder rb = checkStoragePreconditions(datasetEntity, preconditions);
        if (rb == null) {
            // Preconditions passed, serve resource
            DatasetEntityResponse responseEntity = storeDataset(datasetEntity, datasetId, datasetProperties);
            rb = Response.status(responseEntity.getMeta().getStatus())
                    .entity(responseEntity)
                    .tag(Preconditions.createEtag(responseEntity.getData()));
        }
        return rb.build();
    }

    private Response.ResponseBuilder checkStoragePreconditions(final DatasetEntity datasetEntity, Preconditions preconditions) {
        Response.ResponseBuilder rb;
        if (datasetEntity == null) {
            rb = preconditions.evaluatePreconditions();
        } else {
            Dataset existingDataset = DatasetAdaptor.getInstance().convert(datasetEntity);
            Date lastModified = Date.from(datasetEntity.getLastChangedDate().toInstant(ZoneOffset.UTC));
            rb = preconditions.evaluatePreconditions(lastModified, Preconditions.createEtag(existingDataset));
        }

        if (rb != null) {
            rb.entity(new ErrorResponse(Response.Status.PRECONDITION_FAILED, "A request precondition failed."));
        }
        return rb;
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
            submissionService.updateDataset(newDatasetEntity);
        } else {
            dataset = new Dataset();
            dataset.setId(datasetId);
            dataset.setProperties(datasetProperties);

            newDatasetEntity = DatasetAdaptor.getInstance().merge(datasetEntity, dataset);
            submissionService.createDataset(newDatasetEntity);
            status = Response.Status.CREATED;
        }

        dataset.setCreated(Date.from(newDatasetEntity.getCreateDate().toInstant(ZoneOffset.UTC)));

        return new DatasetEntityResponse(status, dataset);
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
            @ApiResponse(code = 404, message = "Not Found - The `dataset_id` parameter did not match a known dataset"),
            @ApiResponse(code = 412, message = "Precondition Failed - see conditional request documentation")

    })
    public Response deleteDataset(
            @PathParam("dataset_id")
            @Pattern(regexp = "[A-Za-z0-9_-]+")
            @ApiParam("The unique identifier for the target dataset") final String datasetId,
            @BeanParam Preconditions preconditions)
            throws Exception {
        Response.ResponseBuilder rb;

        DatasetEntity datasetEntity = submissionService.getDataset(datasetId);
        Dataset dataset = DatasetAdaptor.getInstance().convert(datasetEntity);

        if (datasetEntity == null) {
            rb = Response.status(Response.Status.NOT_FOUND);
        } else {
            EntityTag eTag = Preconditions.createEtag(dataset);

            rb = preconditions.evaluatePreconditions(new Date(), eTag);

            if (rb == null) {
                // Preconditions passed, delete the resource
                submissionService.removeDataset(datasetId);
                rb = Response.status(Response.Status.NO_CONTENT);
            }
        }
        return rb.build();
    }

    /**
     * Retrieve the dataset status information for the specified dataset_id
     *
     * @param datasetId the unique identifier for the target dataset
     * @return
     * @throws Exception if the request cannot be completed normally.
     */
    @GET
    @Path("/{dataset_id}/status")
    @ApiOperation(value = "Retrieve dataset status",
            notes = "**Retrieve the dataset status information for the specified `dataset_id`**\n\n"
                    + "Status information includes details such as any validation errors and the submission status"
    )
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "OK", response = DatasetStatus.class),
            @ApiResponse(code = 404, message = "Not Found - The `dataset_id` parameter did not match a known dataset")
    })
    public Response getDatasetStatus(
            @PathParam("dataset_id")
            @Pattern(regexp = "[A-Za-z0-9_-]+")
            @ApiParam("The unique identifier for the target dataset") final String datasetId)
            throws Exception {

        DatasetStatus testStatus = new DatasetStatus();

        // Submission status
        DatasetSubmissionStatus submissionStatus = new DatasetSubmissionStatus();
        submissionStatus.setStatus(DatasetSubmissionStatus.Status.UNSUBMITTED);
        testStatus.setSubmission(submissionStatus);

        // Substitution status
        DatasetSubstitutions substitutions = new DatasetSubstitutions();
        for (int i = 0; i < 20; i += 2 + Math.round(Math.random())) {
            substitutions.addSubstitution("record" + i, "EA_ID", "12345", "AB1234CD");
            substitutions.addSubstitution("record" + (i + 1), "EA_ID", "98765", "ZY9876BA");
        }
        for (int i = 0; i < 20; i += 1 + Math.round(Math.random() * 2)) {
            substitutions.addSubstitution("record" + i, "Parameter", "CO2", "Carbon Dioxide");
        }
        testStatus.setSubstitutions(substitutions);

        // Validation status
        // TODO
        return Response.status(Response.Status.OK).entity(testStatus).build();
    }

    /**
     * Attempt to set dataset status information
     *
     * @param datasetId the unique identifier for the target dataset
     * @return
     * @throws Exception if the request cannot be completed normally.
     */
    @PUT
    @Path("/{dataset_id}/status")
    @ApiOperation(value = "Set dataset status",
            notes = "**Set the dataset status information for the specified `dataset_id`**\n\n"
                    + "- can be used to submit a dataset to the environment agency"

    )
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "OK", response = DatasetStatus.class),
            @ApiResponse(code = 404, message = "Not Found - The `dataset_id` parameter did not match a known dataset")
    })
    public Response setDatasetStatus(
            @PathParam("dataset_id")
            @Pattern(regexp = "[A-Za-z0-9_-]+")
            @ApiParam("The unique identifier for the target dataset") final String datasetId,
            @ApiParam("The requested status.") final DatasetSubmissionStatus status)
            throws Exception {
        return Response.status(Response.Status.OK).entity("magic").build();
    }
}