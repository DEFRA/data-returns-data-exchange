package uk.gov.defra.datareturns.data.model.regime;

import org.springframework.data.rest.core.annotation.RepositoryRestResource;
import uk.gov.defra.datareturns.data.model.MasterDataRepository;


/**
 * Spring REST repository for {@link Regime} entities
 *
 * @author Sam Gardner-Dell
 */
@RepositoryRestResource
public interface RegimeRepository extends MasterDataRepository<Regime> {
}
