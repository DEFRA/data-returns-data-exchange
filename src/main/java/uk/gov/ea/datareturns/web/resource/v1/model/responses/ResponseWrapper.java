package uk.gov.ea.datareturns.web.resource.v1.model.responses;

import io.swagger.annotations.ApiModel;

import javax.ws.rs.core.Response;

/**
 * Base class for API responses, provides a consistent response envelope
 * @param <T>
 *
 * @author Sam Gardner-Dell
 */
@ApiModel(value = "Default response wrapper object")
public abstract class ResponseWrapper<T> {
    private Metadata meta;
    private T data;

    public ResponseWrapper() {
    }

    public ResponseWrapper(Response.Status status, T data) {
        this.meta = new Metadata(status.getStatusCode());
        this.data = data;
    }

    public Metadata getMeta() {
        return meta;
    }

    public void setMeta(Metadata meta) {
        this.meta = meta;
    }

    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }
}
