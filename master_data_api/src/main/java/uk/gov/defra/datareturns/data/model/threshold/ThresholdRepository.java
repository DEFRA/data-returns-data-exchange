package uk.gov.defra.datareturns.data.model.threshold;

import org.springframework.data.rest.core.annotation.RepositoryRestResource;
import uk.gov.defra.datareturns.data.BaseRepository;


/**
 * Spring REST repository for {@link Threshold} entities
 *
 * @author Sam Gardner-Dell
 */
@RepositoryRestResource
public interface ThresholdRepository extends BaseRepository<Threshold, Long> {
}
