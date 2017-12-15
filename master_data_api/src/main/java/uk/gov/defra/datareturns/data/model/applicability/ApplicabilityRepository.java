package uk.gov.defra.datareturns.data.model.applicability;

import org.springframework.data.rest.core.annotation.RepositoryRestResource;
import uk.gov.defra.datareturns.data.model.MasterDataRepository;


/**
 * Spring REST repository for {@link Applicability} entities
 *
 * @author Sam Gardner-Dell
 */
@RepositoryRestResource
public interface ApplicabilityRepository extends MasterDataRepository<Applicability> {
}
