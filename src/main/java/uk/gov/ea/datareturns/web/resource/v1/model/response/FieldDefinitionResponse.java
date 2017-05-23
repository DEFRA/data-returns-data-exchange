package uk.gov.ea.datareturns.web.resource.v1.model.response;

import io.swagger.annotations.ApiModel;
import uk.gov.ea.datareturns.web.resource.v1.model.definitions.FieldDefinition;

import javax.ws.rs.core.Response;

/**
 * FieldDefinition entity response
 *
 * @author Sam Gardner-Dell
 */
@ApiModel(parent = ResponseWrapper.class)
public class FieldDefinitionResponse extends ResponseWrapper<FieldDefinition> {
    private FieldDefinition data;

    public FieldDefinitionResponse() {
        super();
    }

    public FieldDefinitionResponse(Response.Status status, FieldDefinition data) {
        super(status);
        this.data = data;
    }

    @Override public FieldDefinition getData() {
        return data;
    }

    @Override public void setData(FieldDefinition data) {
        this.data = data;
    }
}
