package uk.gov.ea.datareturns.domain.jpa.entities.userdata;

import uk.gov.ea.datareturns.domain.jpa.entities.userdata.impl.Record;

/**
 * @author Graham Willis
 */
public interface SubmissionType {
    Record getRecord();
    void setRecord(Record record);
}
