package uk.gov.ea.datareturns.web.resource.v1.model.response;

import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlElementWrapper;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import uk.gov.ea.datareturns.web.resource.v1.model.common.references.PayloadReference;

import javax.ws.rs.core.Response;
import java.util.Collection;

/**
 * Payload list response
 *
 * @author Sam Gardner-Dell
 */
@ApiModel(parent = ResponseWrapper.class)
public class PayloadListResponse extends ResponseWrapper<Collection<PayloadReference>> {
    private Collection<PayloadReference> data;

    public PayloadListResponse() {

    }
    public PayloadListResponse(Collection<PayloadReference> data) {
        super(Response.Status.OK);
        this.data = data;
    }

    @ApiModelProperty(name = "data")
    @JacksonXmlElementWrapper(localName = "data")
    @JacksonXmlProperty(localName = "payload")
    @Override public Collection<PayloadReference> getData() {
        return data;
    }

    @Override public void setData(Collection<PayloadReference> data) {
        this.data = data;
    }
}
