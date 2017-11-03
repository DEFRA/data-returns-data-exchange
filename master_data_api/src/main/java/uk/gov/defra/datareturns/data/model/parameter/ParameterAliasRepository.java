package uk.gov.defra.datareturns.data.model.parameter;

import org.springframework.data.rest.core.annotation.RepositoryRestResource;
import org.springframework.stereotype.Repository;
import uk.gov.defra.datareturns.data.model.MasterDataRepository;


/**
 * Spring REST repository for {@link ParameterAlias} entities
 *
 * @author Sam Gardner-Dell
 */
@Repository
public interface ParameterAliasRepository extends MasterDataRepository<ParameterAlias> {
}
