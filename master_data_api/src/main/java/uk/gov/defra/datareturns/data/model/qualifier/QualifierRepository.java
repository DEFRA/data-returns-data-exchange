package uk.gov.defra.datareturns.data.model.qualifier;

import org.springframework.data.rest.core.annotation.RepositoryRestResource;
import uk.gov.defra.datareturns.data.model.MasterDataRepository;


/**
 * Spring REST repository for {@link Qualifier} entities
 *
 * @author Sam Gardner-Dell
 */
@RepositoryRestResource
public interface QualifierRepository extends MasterDataRepository<Qualifier> {
}
