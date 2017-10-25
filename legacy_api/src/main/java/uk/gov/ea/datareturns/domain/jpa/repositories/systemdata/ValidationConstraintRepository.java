package uk.gov.ea.datareturns.domain.jpa.repositories.systemdata;

import org.springframework.stereotype.Repository;
import uk.gov.ea.datareturns.domain.jpa.entities.userdata.impl.PayloadType;
import uk.gov.ea.datareturns.domain.jpa.entities.userdata.impl.ValidationError;
import uk.gov.ea.datareturns.domain.jpa.entities.userdata.impl.ValidationErrorId;
import uk.gov.ea.datareturns.domain.jpa.repositories.BaseRepository;

import java.util.List;

@Repository public interface ValidationConstraintRepository extends BaseRepository<ValidationError, ValidationErrorId> {
    /**
     * Retrieve the list of {@link ValidationError} definitions for the given {@link PayloadType}
     * @param payloadType the payload type
     * @return the list for the given payload type
     */
    List<ValidationError> findAllByIdPayloadType(PayloadType payloadType);
}
