package uk.gov.defra.datareturns.data.model.releaseortransfer;

import org.springframework.data.rest.core.annotation.RepositoryRestResource;
import uk.gov.defra.datareturns.data.model.MasterDataRepository;


/**
 * Spring REST repository for {@link ReleaseOrTransfer} entities
 *
 * @author Sam Gardner-Dell
 */
@RepositoryRestResource
public interface ReleaseOrTransferRepository extends MasterDataRepository<ReleaseOrTransfer> {
}
