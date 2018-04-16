package uk.gov.defra.datareturns.data.model.transfers;

import org.springframework.data.rest.core.annotation.RepositoryRestResource;
import uk.gov.defra.datareturns.data.BaseRepository;


/**
 * Spring REST repository for {@link Transfer} entities
 *
 * @author Sam Gardner-Dell
 */
@RepositoryRestResource
public interface TransferRepository extends BaseRepository<Transfer, Long> {
}
