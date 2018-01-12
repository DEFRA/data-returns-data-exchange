package uk.gov.defra.datareturns.data.model.releases;

import org.springframework.data.rest.core.annotation.RepositoryRestResource;
import uk.gov.defra.datareturns.data.BaseRepository;


/**
 * Spring REST repository for {@link ReleaseToControlledWater} entities
 *
 * @author Sam Gardner-Dell
 */
@RepositoryRestResource(
        path = "releasesToControlledWater",
        collectionResourceRel = "releasesToControlledWater",
        itemResourceRel = "releaseToControlledWater"
)
public interface ReleaseToControlledWaterRepository extends BaseRepository<ReleaseToControlledWater, Long> {
}
