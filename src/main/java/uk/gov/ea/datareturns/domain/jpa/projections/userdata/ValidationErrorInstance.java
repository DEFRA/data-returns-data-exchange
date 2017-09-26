package uk.gov.ea.datareturns.domain.jpa.projections.userdata;

/**
 * Projection to describe validation error instances
 *
 * @author Sam Gardner-Dell
 */
public interface ValidationErrorInstance {
    /**
     * @return the identifier for the record containing the error
     */
    String getRecordIdentifier();

    /**
     * @return the payload type of the record containing the error
     */
    String getPayloadType();

    /**
     * @return the identifier of the constraint that was validated
     */
    String getConstraintIdentifier();
}
