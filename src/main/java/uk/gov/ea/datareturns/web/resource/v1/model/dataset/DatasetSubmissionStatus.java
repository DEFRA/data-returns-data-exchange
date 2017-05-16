package uk.gov.ea.datareturns.web.resource.v1.model.dataset;

/**
 * Dataset submissions status
 *
 * @author Sam Gardner-Dell
 */
public class DatasetSubmissionStatus {
    private Status status;

    public DatasetSubmissionStatus() {

    }

    public Status getStatus() {
        return status;
    }

    public void setStatus(Status status) {
        this.status = status;
    }

    public enum Status {
        UNSUBMITTED, SUBMITTED, RECEIVED
    }
}
