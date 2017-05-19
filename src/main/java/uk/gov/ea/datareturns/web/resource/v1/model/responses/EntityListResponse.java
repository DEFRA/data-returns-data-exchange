package uk.gov.ea.datareturns.web.resource.v1.model.responses;

import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlElementWrapper;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import uk.gov.ea.datareturns.web.resource.v1.model.common.references.EntityReference;

import javax.ws.rs.core.Response;
import java.util.List;

/**
 * DatasetEntity list response
 *
 * @author Sam Gardner-Dell
 */
@ApiModel(parent = ResponseWrapper.class)
public class EntityListResponse extends ResponseWrapper<List<EntityReference>> {
    private List<EntityReference> data;

    public EntityListResponse() {

    }

    public EntityListResponse(List<EntityReference> data) {
        super(Response.Status.OK);
        this.data = data;
    }

    @ApiModelProperty(name = "data")
    @JacksonXmlElementWrapper(localName = "data")
    @JacksonXmlProperty(localName = "reference")
    @Override public List<EntityReference> getData() {
        return data;
    }

    @Override public void setData(List<EntityReference> data) {
        this.data = data;
    }
}
