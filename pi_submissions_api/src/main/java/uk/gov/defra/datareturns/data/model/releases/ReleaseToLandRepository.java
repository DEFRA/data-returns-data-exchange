package uk.gov.defra.datareturns.data.model.releases;

import org.springframework.data.rest.core.annotation.RepositoryRestResource;
import uk.gov.defra.datareturns.data.BaseRepository;


/**
 * Spring REST repository for {@link ReleaseToLand} entities
 *
 * @author Sam Gardner-Dell
 */
@RepositoryRestResource(path = "releasesToLand", collectionResourceRel = "releasesToLand", itemResourceRel = "releaseToLand")
public interface ReleaseToLandRepository extends BaseRepository<ReleaseToLand, Long> {
}
