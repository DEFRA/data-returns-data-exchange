package uk.gov.ea.datareturns.web.resource.v1.model.response;

import io.swagger.annotations.ApiModel;
import uk.gov.ea.datareturns.web.resource.v1.model.eaid.EaId;

import javax.ws.rs.core.Response;

/**
 * @author Graham
 * EA_ID entity response
 */
@ApiModel(parent = ResponseWrapper.class)
public class EaIdEntityResponse extends ResponseWrapper<EaId> {
    private EaId data;

    public EaIdEntityResponse(Response.Status status, EaId data) {
        super(status);
        this.data = data;
    }

    @Override public EaId getData() {
        return data;
    }
    @Override public void setData(EaId data) {
        this.data = data;
    }
}
