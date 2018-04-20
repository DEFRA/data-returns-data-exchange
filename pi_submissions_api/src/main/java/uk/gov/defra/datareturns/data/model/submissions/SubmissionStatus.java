package uk.gov.defra.datareturns.data.model.submissions;

import lombok.Getter;

@Getter
public enum SubmissionStatus {
    Incomplete(true),
    Submitted(false),
    Approved(false),
    Rejected(true);

    private final boolean open;

    SubmissionStatus(final boolean open) {
        this.open = open;
    }
}
