package uk.gov.ea.datareturns.web.resource.v1.model.response;

import io.swagger.annotations.ApiModel;
import uk.gov.ea.datareturns.web.resource.v1.model.dataset.DatasetStatus;

import javax.ws.rs.core.Response;

/**
 * DatasetStatus response wrapper
 *
 * @author Sam Gardner-Dell
 */
@ApiModel(parent = ResponseWrapper.class)
public class DatasetStatusResponse extends ResponseWrapper<DatasetStatus> {
    private DatasetStatus data;

    public DatasetStatusResponse() {
        super();
    }

    public DatasetStatusResponse(DatasetStatus data) {
        super(Response.Status.OK);
        this.data = data;
    }

    @Override public DatasetStatus getData() {
        return data;
    }

    @Override public void setData(DatasetStatus data) {
        this.data = data;
    }
}