package uk.gov.ea.datareturns.web.resource.v1.model.responses.multistatus;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonGetter;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlElementWrapper;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import io.swagger.annotations.ApiModelProperty;
import org.apache.commons.lang3.StringUtils;
import org.springframework.http.HttpStatus;
import uk.gov.ea.datareturns.web.resource.v1.model.responses.ResponseWrapper;

import javax.xml.bind.annotation.XmlRootElement;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Multi-status response used when handling batch POST requests.
 *
 * @author Sam Gardner-Dell
 */
@XmlRootElement(name = "multistatus")
public class MultiStatusResponse extends ResponseWrapper<List<MultiStatusResponse.Response>> {
    private List<Response> data;

    public MultiStatusResponse() {
        super(207);
        this.data = new ArrayList<>();
    }

    @ApiModelProperty(name = "data")
    @JacksonXmlElementWrapper(localName = "data")
    @JacksonXmlProperty(localName = "response")
    @Override public List<Response> getData() {
        return data;
    }

    @Override public void setData(List<Response> data) {
        this.data = data;
    }

    public void addResponse(Response response) {
        this.data.add(response);
    }

    @XmlRootElement(name = "response")
    public static class Response {
        @ApiModelProperty
        @JsonProperty("id")
        private String id;
        @ApiModelProperty
        @JsonProperty("code")
        private int code;
        @ApiModelProperty
        @JsonProperty("href")
        private String href;
        @ApiModelProperty
        @JsonProperty("etag")
        private String entityTag;
        @ApiModelProperty
        @JsonProperty("last_modified")
        @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", timezone = "GMT")
        private Date lastModified;

        public Response() {

        }

        @ApiModelProperty(name = "status", readOnly = true, example = "HTTP/1.1 201 Created")
        @JsonGetter("status")
        public String getStatus() {
            String status = "HTTP/1.1 " + code;

            String reason = HttpStatus.valueOf(code).getReasonPhrase();
            if (StringUtils.isNotEmpty(reason)) {
                status += " " + reason;
            }
            return status;
        }

        public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
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

        public String getEntityTag() {
            return entityTag;
        }

        public void setEntityTag(String entityTag) {
            this.entityTag = entityTag;
        }

        public Date getLastModified() {
            return lastModified;
        }

        public void setLastModified(Date lastModified) {
            this.lastModified = lastModified;
        }
    }
}