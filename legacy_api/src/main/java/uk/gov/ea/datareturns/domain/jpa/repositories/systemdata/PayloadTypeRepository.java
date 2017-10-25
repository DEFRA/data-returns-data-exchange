package uk.gov.ea.datareturns.domain.jpa.repositories.systemdata;

import org.springframework.stereotype.Repository;
import uk.gov.ea.datareturns.domain.jpa.entities.userdata.impl.PayloadType;
import uk.gov.ea.datareturns.domain.jpa.repositories.BaseRepository;


@Repository public interface PayloadTypeRepository extends BaseRepository<PayloadType, String> {
}
