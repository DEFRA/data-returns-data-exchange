package uk.gov.ea.datareturns.web.resource.v1.model.response;

import io.swagger.annotations.ApiModel;
import uk.gov.ea.datareturns.web.resource.v1.model.definitions.ConstraintDefinition;

import javax.ws.rs.core.Response;

/**
 * ConstraintDefinition entity response
 *
 * @author Sam Gardner-Dell
 */
@ApiModel(parent = ResponseWrapper.class)
public class ConstraintDefinitionResponse extends ResponseWrapper<ConstraintDefinition> {
    private ConstraintDefinition data;

    public ConstraintDefinitionResponse() {
        super();
    }

    public ConstraintDefinitionResponse(ConstraintDefinition data) {
        super(Response.Status.OK);
        this.data = data;
    }

    @Override public ConstraintDefinition getData() {
        return data;
    }

    @Override public void setData(ConstraintDefinition data) {
        this.data = data;
    }
}
