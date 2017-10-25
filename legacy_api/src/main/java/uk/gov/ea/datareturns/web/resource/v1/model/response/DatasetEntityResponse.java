package uk.gov.ea.datareturns.web.resource.v1.model.response;

import io.swagger.annotations.ApiModel;
import uk.gov.ea.datareturns.web.resource.v1.model.dataset.Dataset;

import javax.ws.rs.core.Response;

/**
 * Dataset entity response
 *
 * @author Sam Gardner-Dell
 */
@ApiModel(parent = ResponseWrapper.class)
public class DatasetEntityResponse extends ResponseWrapper<Dataset> {
    private Dataset data;

    public DatasetEntityResponse() {
        super();
    }

    public DatasetEntityResponse(Response.Status status, Dataset data) {
        super(status);
        this.data = data;
    }

    @Override public Dataset getData() {
        return data;
    }

    @Override public void setData(Dataset data) {
        this.data = data;
    }
}
