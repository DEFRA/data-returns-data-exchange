package uk.gov.defra.datareturns.data.model.transfers;

import org.springframework.data.rest.core.annotation.RepositoryRestResource;
import uk.gov.defra.datareturns.data.BaseRepository;


/**
 * Spring REST repository for {@link OverseasWasteTransfer} entities
 *
 * @author Sam Gardner-Dell
 */
@RepositoryRestResource
public interface OverseasWasteTransferRepository extends BaseRepository<OverseasWasteTransfer, Long> {
}
