package uk.gov.ea.datareturns.web.resource.v1.model.responses;

import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.annotations.ApiModelProperty;

/**
 * Response metadata.
 *
 * @author Sam Gardner-Dell
 */
public class Metadata {

    @ApiModelProperty(example = "200", value = "The API HTTP status code")
    private int status;
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private String errorDescription;

    public Metadata() {

    }

    public Metadata(int status) {
        this.status = status;
    }

    public Metadata(int status, String errorDescription) {
        this.status = status;
        this.errorDescription = errorDescription;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public String getErrorDescription() {
        return errorDescription;
    }

    public void setErrorDescription(String errorDescription) {
        this.errorDescription = errorDescription;
    }
}
