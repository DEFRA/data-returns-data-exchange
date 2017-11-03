package uk.gov.defra.datareturns.data.model.methodorstandard;

import org.springframework.data.rest.core.annotation.RepositoryRestResource;
import uk.gov.defra.datareturns.data.model.MasterDataRepository;


/**
 * Spring REST repository for {@link MethodOrStandard} entities
 *
 * @author Sam Gardner-Dell
 */
@RepositoryRestResource
public interface MethodOrStandardRepository extends MasterDataRepository<MethodOrStandard> {
}
