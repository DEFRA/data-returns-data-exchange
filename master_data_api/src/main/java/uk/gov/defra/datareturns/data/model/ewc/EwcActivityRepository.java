package uk.gov.defra.datareturns.data.model.ewc;

import org.springframework.data.rest.core.annotation.RepositoryRestResource;
import uk.gov.defra.datareturns.data.model.MasterDataRepository;

/**
 * Spring REST repository for {@link EwcActivity} entities
 *
 * @author Sam Gardner-Dell
 */
@RepositoryRestResource
public interface EwcActivityRepository extends MasterDataRepository<EwcActivity> {
}
