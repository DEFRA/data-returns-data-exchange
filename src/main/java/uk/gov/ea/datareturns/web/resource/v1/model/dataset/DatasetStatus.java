package uk.gov.ea.datareturns.web.resource.v1.model.dataset;

/**
 * Aggregrate dataset status information
 *
 * @author Sam Gardner-Dell
 */
public class DatasetStatus {

    private DatasetSubmissionStatus submission;


    private DatasetValidationStatus validity;

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
}
