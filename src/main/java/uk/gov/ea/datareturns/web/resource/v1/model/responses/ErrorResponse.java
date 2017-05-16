package uk.gov.ea.datareturns.web.resource.v1.model.responses;

import javax.ws.rs.core.Response;

/**
 * Error response.  Populates response metadata when handling an API request error.
 *
 * @author Sam Gardner-Dell
 */
public class ErrorResponse extends ResponseWrapper<Object> {

    public ErrorResponse(Response.Status status, String error) {
        super();
        this.setMeta(new Metadata(status.getStatusCode(), error));
    }
}
