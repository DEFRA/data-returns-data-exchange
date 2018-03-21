package uk.gov.defra.datareturns.data.model.regimeobligation;

import org.springframework.data.rest.core.annotation.RepositoryRestResource;
import uk.gov.defra.datareturns.data.model.MasterDataRepository;


/**
 * Spring REST repository for {@link RegimeObligation} entities
 *
 * @author Sam Gardner-Dell
 */
@RepositoryRestResource
public interface RegimeObligationRepository extends MasterDataRepository<RegimeObligation> {
}
