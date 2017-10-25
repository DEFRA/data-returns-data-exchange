package uk.gov.ea.datareturns.web.resource.v1.model.response;

import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlElementWrapper;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import uk.gov.ea.datareturns.web.resource.v1.model.definitions.FieldDefinition;

import javax.ws.rs.core.EntityTag;
import javax.ws.rs.core.Response;
import java.util.Date;
import java.util.List;

/**
 * {@link FieldDefinition} list response
 *
 * @author Sam Gardner-Dell
 */
@ApiModel(parent = ResponseWrapper.class)
public class FieldDefinitionListResponse extends ResponseWrapper<List<FieldDefinition>> {
    private List<FieldDefinition> data;

    @ApiModelProperty(hidden = true)
    private Date lastModified;

    @ApiModelProperty(hidden = true)
    private EntityTag entityTag;

    public FieldDefinitionListResponse() {

    }

    public FieldDefinitionListResponse(List<FieldDefinition> data) {
        super(Response.Status.OK);
        this.data = data;
    }

    public FieldDefinitionListResponse(List<FieldDefinition> data, Date lastModified, EntityTag entityTag) {
        super(Response.Status.OK);
        this.data = data;
        this.lastModified = lastModified;
        this.entityTag = entityTag;
    }

    @ApiModelProperty(name = "data")
    @JacksonXmlElementWrapper(localName = "data")
    @JacksonXmlProperty(localName = "reference")
    @Override public List<FieldDefinition> getData() {
        return data;
    }

    @Override public void setData(List<FieldDefinition> data) {
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
