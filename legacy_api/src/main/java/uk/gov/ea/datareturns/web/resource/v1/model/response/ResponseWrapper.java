package uk.gov.ea.datareturns.web.resource.v1.model.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.annotations.ApiModel;
import uk.gov.ea.datareturns.web.resource.v1.model.common.EntityBase;
import uk.gov.ea.datareturns.web.resource.v1.model.common.Preconditions;

import javax.ws.rs.core.Response;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * Base class for API responses, provides a consistent response envelope
 * @param <T>
 *
 * @author Sam Gardner-Dell
 */
@ApiModel(description = "Default response wrapper object")
@XmlRootElement(name = "response")
public abstract class ResponseWrapper<T> {
    private ResponseMetadata meta;

    public ResponseWrapper() {
    }

    public ResponseWrapper(int status) {
        this.meta = new ResponseMetadata(status);
    }

    public ResponseWrapper(Response.Status status) {
        this(status.getStatusCode());
    }

    @JsonProperty("meta")
    public ResponseMetadata getMeta() {
        return meta;
    }

    public void setMeta(ResponseMetadata meta) {
        this.meta = meta;
    }

    @JsonProperty("data")
    public abstract T getData();

    public abstract void setData(T data);


    public final Response.ResponseBuilder toResponseBuilder() {
        Response.ResponseBuilder rb = Response.status(getMeta().getStatus());
        rb.entity(this);
        addModificationHeaders(rb);
        return rb;
    }

    /**
     * Add modification related headers to the response (ETag/Last-Modified)
     * @param rb the responsebuilder to act on.
     *
     * This method provides an override point.
     */
    protected void addModificationHeaders(Response.ResponseBuilder rb) {
        if (getData() instanceof EntityBase) {
            EntityBase entity = (EntityBase) getData();
            rb.tag(Preconditions.createEtag(entity));
            rb.lastModified(entity.getLastModified());
        }
    }
}
