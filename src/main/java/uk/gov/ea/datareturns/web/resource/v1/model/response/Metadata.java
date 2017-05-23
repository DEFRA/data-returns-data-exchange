package uk.gov.ea.datareturns.web.resource.v1.model.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.annotations.ApiModelProperty;

/**
 * Response metadata.
 *
 * @author Sam Gardner-Dell
 */
public class Metadata {
    @ApiModelProperty(example = "200", value = "The API HTTP status code")
    private int status;

    @JsonProperty("error")
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private String error;

    public Metadata() {
    }

    public Metadata(int status) {
        this.status = status;
    }

    public Metadata(int status, String error) {
        this.status = status;
        this.error = error;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public String getError() {
        return error;
    }

    public void setError(String error) {
        this.error = error;
    }
}