package uk.gov.ea.datareturns.web.resource.v1.model.responses.record;

import io.swagger.annotations.ApiModel;
import uk.gov.ea.datareturns.web.resource.v1.model.record.Record;
import uk.gov.ea.datareturns.web.resource.v1.model.responses.ResponseWrapper;

import javax.ws.rs.core.Response;

/**
 * RecordEntity entity response
 *
 * @author Sam Gardner-Dell
 */
@ApiModel(parent = ResponseWrapper.class)
public class RecordEntityResponse extends ResponseWrapper<Record> {
    private Record data;

    public RecordEntityResponse() {
        super();
    }

    public RecordEntityResponse(Response.Status status, Record data) {
        super(status);
        this.data = data;
    }

    @Override public Record getData() {
        return data;
    }

    @Override public void setData(Record data) {
        this.data = data;
    }
}
