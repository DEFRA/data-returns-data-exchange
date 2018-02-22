package uk.gov.defra.datareturns.data.model.eprtr;

import org.springframework.data.rest.core.annotation.RepositoryRestResource;
import uk.gov.defra.datareturns.data.model.MasterDataRepository;
import uk.gov.defra.datareturns.data.model.ewc.EwcActivity;

/**
 * Spring REST repository for {@link EwcActivity} entities
 *
 * @author Sam Gardner-Dell
 */
@RepositoryRestResource
public interface EprtrActivityRepository extends MasterDataRepository<EprtrActivity> {
}
