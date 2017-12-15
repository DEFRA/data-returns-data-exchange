package uk.gov.defra.datareturns.data.model.unit;

import org.springframework.data.rest.core.annotation.RepositoryRestResource;
import uk.gov.defra.datareturns.data.model.MasterDataRepository;


/**
 * Spring REST repository for {@link UnitGroup} entities
 *
 * @author Sam Gardner-Dell
 */
@RepositoryRestResource
public interface UnitGroupRepository extends MasterDataRepository<UnitGroup> {
}
