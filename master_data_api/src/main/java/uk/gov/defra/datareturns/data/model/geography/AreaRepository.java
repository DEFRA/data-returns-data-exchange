package uk.gov.defra.datareturns.data.model.geography;

import org.springframework.data.rest.core.annotation.RepositoryRestResource;
import uk.gov.defra.datareturns.data.model.MasterDataRepository;


/**
 * Spring REST repository for {@link Area} entities
 *
 * @author Sam Gardner-Dell
 */
@RepositoryRestResource
public interface AreaRepository extends MasterDataRepository<Area> {
}
