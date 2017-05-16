package uk.gov.ea.datareturns.web.resource.v1.model.responses.record;

import io.swagger.annotations.ApiModel;
import uk.gov.ea.datareturns.web.resource.v1.model.common.references.EntityReference;
import uk.gov.ea.datareturns.web.resource.v1.model.responses.ResponseWrapper;
import uk.gov.ea.datareturns.web.resource.v1.model.common.ListWrapper;

import javax.ws.rs.core.Response;
import java.util.List;

/**
 * Record listing response
 *
 * @author Sam Gardner-Dell
 */
@ApiModel(parent = ResponseWrapper.class)
public class RecordListResponse extends ResponseWrapper<ListWrapper<EntityReference>> {
    public RecordListResponse(Response.Status status, List<EntityReference> data) {
        super(status, new ListWrapper<>(data));
    }
}
