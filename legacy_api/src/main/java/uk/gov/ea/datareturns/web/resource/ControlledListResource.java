package uk.gov.ea.datareturns.web.resource;

import org.apache.commons.lang3.tuple.Pair;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.NoSuchBeanDefinitionException;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import uk.gov.ea.datareturns.domain.dto.impl.ControlledListsDto;
import uk.gov.ea.datareturns.domain.exceptions.ApplicationExceptionType;
import uk.gov.ea.datareturns.domain.exceptions.ExceptionMessageContainer;
import uk.gov.ea.datareturns.domain.jpa.entities.masterdata.MasterDataEntity;
import uk.gov.ea.datareturns.domain.jpa.entities.masterdata.impl.ControlledListsList;
import uk.gov.ea.datareturns.domain.jpa.hierarchy.Hierarchy;
import uk.gov.ea.datareturns.domain.jpa.hierarchy.HierarchyLevel;
import uk.gov.ea.datareturns.domain.jpa.service.MasterDataLookupService;
import uk.gov.ea.datareturns.domain.processors.ControlledListProcessor;

import javax.inject.Inject;
import javax.ws.rs.*;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static javax.ws.rs.core.MediaType.APPLICATION_JSON;

/**
 * The {@link ControlledListResource} RESTful service to server controlled list definitions
 *
 * @author Graham Willis
 */
@Component
@Path("/controlled-list/")
@Scope(ConfigurableBeanFactory.SCOPE_SINGLETON)
public class ControlledListResource implements JerseyResource {
    private static final Logger LOGGER = LoggerFactory.getLogger(ControlledListResource.class);

    /** controlled list processor */
    private final ControlledListProcessor controlledListProcessor;

    private final ApplicationContext context;

    private final MasterDataLookupService lookupService;

    /**
     * Create a new {@link ControlledListResource} RESTful service
     *
     * @param controlledListProcessor the controlled list processor
     */
    @Inject
    public ControlledListResource(final ApplicationContext context, final ControlledListProcessor controlledListProcessor, final
    MasterDataLookupService lookupService) {
        this.context = context;
        this.controlledListProcessor = controlledListProcessor;
        this.lookupService = lookupService;
    }

    /**
     * Returns a list of the controlled lists - the list metadata
     * @return
     */
    @GET
    @Produces(APPLICATION_JSON)
    @Path("/lists/")
    public Response listControlledLists() {
        LOGGER.debug("Request for /controlled-list");
        Map<String, ControlledListsDto> listData = controlledListProcessor.getListMetaData();
        return Response.status(Response.Status.OK).entity(listData).build();
    }

    /**
     * Returns any given controlled list searched by a given field
     * @param listName
     * @param contains
     * @return
     * @throws Exception
     */
    @GET
    @Path("/lists/{listname}/")
    @Produces(APPLICATION_JSON)
    public Response getControlledList(
            @PathParam("listname") final String listName,
            @QueryParam("contains") final String contains) throws Exception {
        LOGGER.debug("Request for /controlled-list/" + listName + " containing items matching filter: " + contains);
        ControlledListsList controlledList = ControlledListsList.getByPath(listName);

        // Check we have a registered controlled list type
        if (controlledList == null) {
            LOGGER.info("Request for unknown controlled list: " + listName);
            return Response.status(Response.Status.BAD_REQUEST).entity(new ExceptionMessageContainer(
                    ApplicationExceptionType.UNKNOWN_LIST_TYPE, "Request for unknown controlled list: " + listName
            )).build();
        } else {
            Pair<String, List<? extends MasterDataEntity>> listData = controlledListProcessor.getListData(controlledList, contains);
            return Response.status(Response.Status.OK).entity(listData).build();
        }
    }

    /**
     * Returns the list of entities given by the validation rules at any given level in
     * a hierarchy
     *
     * The name of the hierarchy is specified in the path parameter hierarchy and is identified in the spring
     * context by appending the string -hierarchy
     * @param ui
     * @return
     */
    @GET
    @Path("/hierarchy/{name}")
    @Produces(APPLICATION_JSON)
    public Response getHierarchyLevel(@Context UriInfo ui) {
        MultivaluedMap<String, String> queryParams = ui.getQueryParameters();
        MultivaluedMap<String, String> pathParams = ui.getPathParameters();
        try {
            List<String> hierachyNames = pathParams.get("name");
            Hierarchy<?> hierarchy = (Hierarchy) context.getBean(hierachyNames.get(0) + "-hierarchy");
            Set<HierarchyLevel<?>> levels = hierarchy.getHierarchyLevels();
            String field = queryParams.containsKey("field") ? queryParams.get("field").get(0) : null;
            String contains = queryParams.containsKey("contains") ? queryParams.get("contains").get(0) : null;
            Set<MasterDataEntity> entities = getHierarchyEntitiesFromParameters(queryParams, levels);
            Pair<String, List<? extends MasterDataEntity>> controlledList = controlledListProcessor
                    .getListData(hierarchy, entities, field, contains);
            return Response.status(Response.Status.OK).entity(controlledList).build();
        } catch (NoSuchBeanDefinitionException | ClassCastException e) {
            return Response.status(Response.Status.BAD_REQUEST).entity(new ExceptionMessageContainer(
                    ApplicationExceptionType.UNKNOWN_LIST_TYPE,
                    "No hierarchy: you must specify a known hierarchy in the path parameter /controlled-list/hierarchy/{name}")).build();
        }
    }

    /**
     * This end point provides a service to test the validation of a given hierarchy level
     *
     * The name of the hierarchy is specified in the path parameter hierarchy and is identified in the spring
     * context by appending the string -hierarchy
     * @param ui
     * @return
     */
    @GET
    @Path("/hierarchy/{name}/validate")
    @Produces(APPLICATION_JSON)
    public Response testHierarchyLevel
    (@Context UriInfo ui) {
        MultivaluedMap<String, String> queryParams = ui.getQueryParameters();
        MultivaluedMap<String, String> pathParams = ui.getPathParameters();
        try {
            List<String> hierarchyNames = pathParams.get("name");
            Hierarchy<?> hierarchy = (Hierarchy) context.getBean(hierarchyNames.get(0) + "-hierarchy");
            Set<HierarchyLevel<?>> levels = hierarchy.getHierarchyLevels();
            Set<MasterDataEntity> entities = getHierarchyEntitiesFromParameters(queryParams, levels);
            Pair<String, Hierarchy.Result> validationResult = controlledListProcessor.validate(hierarchy, entities);
            return Response.status(Response.Status.OK).entity(validationResult).build();
        } catch (NoSuchBeanDefinitionException | ClassCastException e) {
            return Response.status(Response.Status.BAD_REQUEST).entity(new ExceptionMessageContainer(
                    ApplicationExceptionType.UNKNOWN_LIST_TYPE,
                    "No hierarchy: you must specify a known hierarchy in the path parameter /controlled-list/hierarchy/{name}")).build();
        }
    }

    /**
     * The query params should correspond to the path variable stored against the controlled list
     * @param queryParams
     * @param levels
     * @return
     */
    private Set<MasterDataEntity> getHierarchyEntitiesFromParameters(MultivaluedMap<String, String> queryParams,
            Set<HierarchyLevel<?>> levels) {
        Set<MasterDataEntity> entities = new HashSet<>();
        for (HierarchyLevel<? extends MasterDataEntity> level : levels) {
            String path = level.getControlledList().getPath();
            if (queryParams.containsKey(path)) {
                MasterDataEntity entity = lookupService.relaxed().find(level.getHierarchyEntityClass(), queryParams.getFirst(path));
                if (entity != null) {
                    entities.add(entity);
                }
            }
        }
        return entities;
    }
}
