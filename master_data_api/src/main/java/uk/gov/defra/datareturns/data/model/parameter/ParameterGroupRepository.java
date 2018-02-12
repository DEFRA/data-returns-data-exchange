package uk.gov.defra.datareturns.data.model.parameter;

import org.springframework.data.rest.core.annotation.RepositoryRestResource;
import uk.gov.defra.datareturns.data.model.MasterDataRepository;


/**
 * Spring REST repository for {@link ParameterGroup} entities
 *
 * @author Sam Gardner-Dell
 */
@RepositoryRestResource
public interface ParameterGroupRepository extends MasterDataRepository<ParameterGroup> {
}
