package uk.gov.ea.datareturns.web.resource.v1.model.response;

import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlElementWrapper;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

import javax.ws.rs.core.Response;
import java.util.Collection;

/**
 * Payload list response
 *
 * @author Sam Gardner-Dell
 */
@ApiModel(parent = ResponseWrapper.class)
public class PayloadListResponse extends ResponseWrapper<Collection<String>> {
    private Collection<String> data;

    public PayloadListResponse() {

    }
    public PayloadListResponse(Collection<String> data) {
        super(Response.Status.OK);
        this.data = data;
    }

    @ApiModelProperty(name = "data")
    @JacksonXmlElementWrapper(localName = "data")
    @JacksonXmlProperty(localName = "payload_type")
    @Override public Collection<String> getData() {
        return data;
    }

    @Override public void setData(Collection<String> data) {
        this.data = data;
    }
}
