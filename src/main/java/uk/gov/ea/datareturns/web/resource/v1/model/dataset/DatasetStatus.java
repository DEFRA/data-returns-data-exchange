package uk.gov.ea.datareturns.web.resource.v1.model.dataset;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonUnwrapped;

/**
 * Aggregrate dataset status information
 *
 * @author Sam Gardner-Dell
 */
public class DatasetStatus {

    private DatasetSubmissionStatus submission;

    private DatasetValidationStatus validity;

    @JsonProperty("substitutions")
    @JsonUnwrapped
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private DatasetSubstitutions substitutions;

    public DatasetStatus() {

    }

    public DatasetSubmissionStatus getSubmission() {
        return submission;
    }

    public void setSubmission(DatasetSubmissionStatus submission) {
        this.submission = submission;
    }

    public DatasetValidationStatus getValidity() {
        return validity;
    }

    public void setValidity(DatasetValidationStatus validity) {
        this.validity = validity;
    }

    public DatasetSubstitutions getSubstitutions() {
        return substitutions;
    }

    public void setSubstitutions(DatasetSubstitutions substitutions) {
        this.substitutions = substitutions;
    }
}
