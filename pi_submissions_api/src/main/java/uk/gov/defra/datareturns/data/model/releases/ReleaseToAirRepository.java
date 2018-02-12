package uk.gov.defra.datareturns.data.model.releases;

import org.springframework.data.rest.core.annotation.RepositoryRestResource;
import uk.gov.defra.datareturns.data.BaseRepository;


/**
 * Spring REST repository for {@link ReleaseToAir} entities
 *
 * @author Sam Gardner-Dell
 */
@RepositoryRestResource(path = "releasesToAir", collectionResourceRel = "releasesToAir", itemResourceRel = "releaseToAir")
public interface ReleaseToAirRepository extends BaseRepository<ReleaseToAir, Long> {
}
