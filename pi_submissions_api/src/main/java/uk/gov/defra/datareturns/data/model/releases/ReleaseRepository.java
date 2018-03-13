package uk.gov.defra.datareturns.data.model.releases;

import org.springframework.data.rest.core.annotation.RepositoryRestResource;
import uk.gov.defra.datareturns.data.BaseRepository;


/**
 * Spring REST repository for {@link Release} entities
 *
 * @author Sam Gardner-Dell
 */
@RepositoryRestResource
public interface ReleaseRepository extends BaseRepository<Release, Long> {
}
