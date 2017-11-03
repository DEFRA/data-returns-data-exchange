package uk.gov.defra.datareturns.data.model.eaid;

import org.springframework.data.rest.core.annotation.RepositoryRestResource;
import uk.gov.defra.datareturns.data.model.MasterDataRepository;

/**
 * Spring REST repository for {@link UniqueIdentifierAlias} entities
 *
 * @author Sam Gardner-Dell
 */
@RepositoryRestResource(exported = false)
public interface UniqueIdentifierAliasRepository extends MasterDataRepository<UniqueIdentifierAlias> {

}
