package uk.gov.defra.datareturns.data.model.returntype;

import org.springframework.data.rest.core.annotation.RepositoryRestResource;
import uk.gov.defra.datareturns.data.model.MasterDataRepository;

/**
 * Spring REST repository for {@link ReturnType} entities
 *
 * @author Sam Gardner-Dell
 */
@RepositoryRestResource
public interface ReturnTypeRepository extends MasterDataRepository<ReturnType> {
}
