package uk.gov.defra.datareturns.data.model.referenceperiod;

import org.springframework.data.rest.core.annotation.RepositoryRestResource;
import uk.gov.defra.datareturns.data.model.MasterDataRepository;
import uk.gov.defra.datareturns.data.model.parameter.ParameterAlias;


/**
 * Spring REST repository for {@link ReferencePeriodAlias} entities
 *
 * @author Sam Gardner-Dell
 */
@RepositoryRestResource(exported = false)
public interface ReferencePeriodAliasRepository extends MasterDataRepository<ReferencePeriodAlias> {
}
