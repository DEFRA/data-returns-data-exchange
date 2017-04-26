package uk.gov.ea.datareturns.domain.model;

import uk.gov.ea.datareturns.domain.jpa.entities.userdata.SubmissionType;

/**
 * @author Graham Willis
 * Interface representing the payload for submissions to the API via the
 * RESTful interface. The current and only implmenting class is Datasample
 * which defines the dataset for landfill returns.
 */
public interface Payload<T extends SubmissionType> {
    T toSubmissionType();
}
