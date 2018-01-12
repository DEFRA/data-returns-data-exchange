package uk.gov.defra.datareturns.data.model.releases;

import org.springframework.data.rest.core.annotation.RepositoryRestResource;
import uk.gov.defra.datareturns.data.BaseRepository;


/**
 * Spring REST repository for {@link ReleaseToWasteWater} entities
 *
 * @author Sam Gardner-Dell
 */
@RepositoryRestResource(
        path = "releasesToWasteWater",
        collectionResourceRel = "releasesToWasteWater",
        itemResourceRel = "releaseToWasteWater"
)
public interface ReleaseToWasteWaterRepository extends BaseRepository<ReleaseToWasteWater, Long> {
}
