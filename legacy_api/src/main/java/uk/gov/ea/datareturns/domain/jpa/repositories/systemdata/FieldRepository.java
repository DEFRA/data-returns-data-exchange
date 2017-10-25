package uk.gov.ea.datareturns.domain.jpa.repositories.systemdata;

import org.springframework.stereotype.Repository;
import uk.gov.ea.datareturns.domain.jpa.entities.userdata.impl.FieldEntity;
import uk.gov.ea.datareturns.domain.jpa.entities.userdata.impl.FieldId;
import uk.gov.ea.datareturns.domain.jpa.entities.userdata.impl.PayloadType;
import uk.gov.ea.datareturns.domain.jpa.repositories.BaseRepository;

import java.util.List;

@Repository public interface FieldRepository extends BaseRepository<FieldEntity, FieldId> {
    /**
     * Retrieve the list of {@link FieldEntity} objects which related to the given {@link PayloadType}
     * @param payloadType the payload type
     * @return the list of field entities for the given payload type
     */
    List<FieldEntity> findAllByIdPayloadType(PayloadType payloadType);
}
