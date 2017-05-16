package uk.gov.ea.datareturns.web.resource.v1.model.responses.multistatus;

import com.fasterxml.jackson.annotation.JsonGetter;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlElementWrapper;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import io.swagger.annotations.ApiModelProperty;
import org.apache.commons.lang3.StringUtils;

import javax.xml.bind.annotation.XmlRootElement;
import java.util.ArrayList;
import java.util.List;

/**
 * Multi-status response used when handling batch POST requests.
 *
 * @author Sam Gardner-Dell
 */
@XmlRootElement(name = "multistatus")
public class MultiStatusResponse {
    @ApiModelProperty
    @JacksonXmlElementWrapper(useWrapping = false)
    @JacksonXmlProperty(localName = "response")
    private List<Response> responses;

    public MultiStatusResponse() {
        this.responses = new ArrayList<>();
    }

    public List<Response> getResponses() {
        return responses;
    }

    public void setResponses(List<Response> responses) {
        this.responses = responses;
    }

    public void addResponse(Response response) {
        this.responses.add(response);
    }

    @XmlRootElement(name = "response")
    public static class Response {
        @ApiModelProperty
        private String id;
        @ApiModelProperty
        private int code;
        @ApiModelProperty
        private String href;

        public Response() {

        }

        @ApiModelProperty(name = "status", readOnly = true, example = "HTTP/1.1 201 Created")
        @JsonGetter("status")
        public String getStatus() {
            String status = "HTTP/1.1 " + code;

            String reason = javax.ws.rs.core.Response.Status.fromStatusCode(code).getReasonPhrase();
            if (StringUtils.isNotEmpty(reason)) {
                status += " " + reason;
            }
            return status;
        }

        public int getCode() {
            return code;
        }

        public void setCode(int code) {
            this.code = code;
        }

        public String getHref() {
            return href;
        }

        public void setHref(String href) {
            this.href = href;
        }

        public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
        }
    }

}
