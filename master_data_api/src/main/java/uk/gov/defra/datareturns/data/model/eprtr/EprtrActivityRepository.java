package uk.gov.defra.datareturns.data.model.eprtr;

import org.springframework.data.rest.core.annotation.RepositoryRestResource;
import uk.gov.defra.datareturns.data.model.MasterDataRepository;

/**
 * Spring REST repository for {@link EprtrActivity} entities
 *
 * @author Sam Gardner-Dell
 */
@RepositoryRestResource
public interface EprtrActivityRepository extends MasterDataRepository<EprtrActivity> {
}
