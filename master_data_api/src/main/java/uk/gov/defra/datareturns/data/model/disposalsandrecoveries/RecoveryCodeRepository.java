package uk.gov.defra.datareturns.data.model.disposalsandrecoveries;

import org.springframework.data.rest.core.annotation.RepositoryRestResource;
import uk.gov.defra.datareturns.data.model.MasterDataRepository;

/**
 * Spring REST repository for {@link RecoveryCode} entities
 *
 * @author Druid Wood
 */
@RepositoryRestResource
public interface RecoveryCodeRepository extends MasterDataRepository<RecoveryCode> {
}
