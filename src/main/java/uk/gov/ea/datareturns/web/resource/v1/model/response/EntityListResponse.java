package uk.gov.ea.datareturns.web.resource.v1.model.response;

import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlElementWrapper;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import uk.gov.ea.datareturns.web.resource.v1.model.common.references.EntityReference;

import javax.ws.rs.core.EntityTag;
import javax.ws.rs.core.Response;
import java.util.Date;
import java.util.List;

/**
 * EntityReference list response
 *
 * @author Sam Gardner-Dell
 */
@ApiModel(parent = ResponseWrapper.class)
public class EntityListResponse extends ResponseWrapper<List<EntityReference>> {
    private List<EntityReference> data;

    @ApiModelProperty(hidden = true)
    private Date lastModified;

    @ApiModelProperty(hidden = true)
    private EntityTag entityTag;

    public EntityListResponse() {

    }

    public EntityListResponse(List<EntityReference> data) {
        super(Response.Status.OK);
        this.data = data;
    }

    public EntityListResponse(List<EntityReference> data, Date lastModified, EntityTag entityTag) {
        super(Response.Status.OK);
        this.data = data;
        this.lastModified = lastModified;
        this.entityTag = entityTag;
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

    @Override
    protected void addModificationHeaders(Response.ResponseBuilder rb) {
        if (this.entityTag != null) {
            rb.tag(this.entityTag);
        }
        if (this.lastModified != null) {
            rb.lastModified(this.lastModified);
        }
    }
}
