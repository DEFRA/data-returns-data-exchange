package uk.gov.ea.datareturns.web.resource;

import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import uk.gov.ea.datareturns.util.TestDataInitialization;

import javax.inject.Inject;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.core.Response;

/**
 * @author Graham Willis
 *
 * Endpoints for maintenance of controlled lists and permits
 */
@Path("/maintenance/")
@Component
@Scope(ConfigurableBeanFactory.SCOPE_SINGLETON)
public class DataMaintenanceResource {

    private final TestDataInitialization testDataInitialization;

    @Inject
    public DataMaintenanceResource(TestDataInitialization testDataInitialization) {
        this.testDataInitialization = testDataInitialization;
    }

    @GET
    @Path("/test-add")
    public Response testDataAdd() {
        testDataInitialization.setUpTestData();
        return Response.status(Response.Status.OK).build();
    }

    @GET
    @Path("/test-rem")
    public Response testDataRemove() {
        testDataInitialization.tearDownTestData();
        return Response.status(Response.Status.OK).build();
    }

}
