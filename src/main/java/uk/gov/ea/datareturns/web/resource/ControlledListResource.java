package uk.gov.ea.datareturns.web.resource;

import org.apache.commons.lang3.tuple.Pair;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.NoSuchBeanDefinitionException;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import uk.gov.ea.datareturns.domain.dto.ControlledListsDto;
import uk.gov.ea.datareturns.domain.exceptions.ApplicationExceptionType;
import uk.gov.ea.datareturns.domain.jpa.dao.EntityDao;
import uk.gov.ea.datareturns.domain.jpa.dao.Key;
import uk.gov.ea.datareturns.domain.jpa.entities.ControlledListEntity;
import uk.gov.ea.datareturns.domain.jpa.entities.ControlledListsList;
import uk.gov.ea.datareturns.domain.jpa.hierarchy.Hierarchy;
import uk.gov.ea.datareturns.domain.jpa.hierarchy.HierarchyLevel;
import uk.gov.ea.datareturns.domain.processors.ControlledListProcessor;
import uk.gov.ea.datareturns.domain.result.ExceptionMessageContainer;
import uk.gov.ea.datareturns.util.SpringApplicationContextProvider;

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
public class ControlledListResource {
    private static final Logger LOGGER = LoggerFactory.getLogger(ControlledListResource.class);

    /** controlled list processor */
    private final ControlledListProcessor controlledListProcessor;

    /**
     * Create a new {@link ControlledListResource} RESTful service
     *
     * @param controlledListProcessor the controlled list processor
     */
    @Inject
    public ControlledListResource(final ControlledListProcessor controlledListProcessor) {
        this.controlledListProcessor = controlledListProcessor;
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
     * @param field
     * @param contains
     * @return
     * @throws Exception
     */
    @GET
    @Path("/lists/{listname}/")
    @Produces(APPLICATION_JSON)
    public Response getControlledList(
            @PathParam("listname") final String listName,
            @QueryParam("field") final String field,
            @QueryParam("contains") final String contains) throws Exception {
        LOGGER.debug("Request for /controlled-list/" + listName + " Field: " + field + " contains: " + contains);
        ControlledListsList controlledList = ControlledListsList.getByPath(listName);

        // Check we have a registered controlled list type
        if (controlledList == null) {
            LOGGER.error("Request for unknown controlled list: " + listName);
            return Response.status(Response.Status.BAD_REQUEST).entity(new ExceptionMessageContainer(
                    ApplicationExceptionType.UNKNOWN_LIST_TYPE, "Request for unknown controlled list: " + listName
            )).build();
        } else {
            Pair<String, List<? extends ControlledListEntity>> listData = controlledListProcessor.getListData(controlledList, field, contains);
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
            ApplicationContext context = SpringApplicationContextProvider.getApplicationContext();
            List<String> hierachyNames = pathParams.get("name");
            Hierarchy hierarchy = (Hierarchy) context.getBean(hierachyNames.get(0) + "-hierarchy");
            Set<HierarchyLevel> levels = hierarchy.getHierarchyLevels();
            String field = queryParams.containsKey("field") ? ((List<String>)queryParams.get("field")).get(0) : null;
            String contains = queryParams.containsKey("contains") ? ((List<String>)queryParams.get("contains")).get(0) : null;
            Set<Hierarchy.HierarchyEntity> entities = getHierarchyEntitiesFromParameters(queryParams, context, levels);
            Pair<String, List<? extends Hierarchy.HierarchyEntity>> controlledList = controlledListProcessor.getListData(hierarchy, entities, field, contains);
            return Response.status(Response.Status.OK).entity(controlledList).build();
        } catch (NoSuchBeanDefinitionException|ClassCastException e) {
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
            ApplicationContext context = SpringApplicationContextProvider.getApplicationContext();
            List<String> hierarchyNames = pathParams.get("name");
            Hierarchy hierarchy = (Hierarchy) context.getBean(hierarchyNames.get(0) + "-hierarchy");
            Set<HierarchyLevel> levels = hierarchy.getHierarchyLevels();
            Set<Hierarchy.HierarchyEntity> entities = getHierarchyEntitiesFromParameters(queryParams, context, levels);
            Pair<String, Hierarchy.Result> validationResult = controlledListProcessor.validate(hierarchy, entities);
            return Response.status(Response.Status.OK).entity(validationResult).build();
        } catch (NoSuchBeanDefinitionException|ClassCastException e) {
            return Response.status(Response.Status.BAD_REQUEST).entity(new ExceptionMessageContainer(
                    ApplicationExceptionType.UNKNOWN_LIST_TYPE,
                    "No hierarchy: you must specify a known hierarchy in the path parameter /controlled-list/hierarchy/{name}")).build();
        }
    }

    /**
     * The query params should correspond to the path variable stored against the controlled list
     * @param queryParams
     * @param context
     * @param levels
     * @return
     */
    private Set<Hierarchy.HierarchyEntity> getHierarchyEntitiesFromParameters(MultivaluedMap<String, String> queryParams, ApplicationContext context, Set<HierarchyLevel> levels) {
        Set<Hierarchy.HierarchyEntity> entities = new HashSet<>();
        for (HierarchyLevel<? extends Hierarchy.HierarchyEntity> level : levels) {
            String path = level.getControlledList().getPath();
            if (queryParams.containsKey(path)) {
                Class<? extends EntityDao<? extends Hierarchy.HierarchyEntity>> daoClass = level.getDaoClass();
                EntityDao<? extends Hierarchy.HierarchyEntity> dao = context.getBean(daoClass);
                Hierarchy.HierarchyEntity entity = dao.getByName(Key.relaxed(queryParams.getFirst(path)));
                if (entity != null) {
                    entities.add(entity);
                }
            }
        }
        return entities;
    }

 }
