package uk.gov.defra.datareturns.data.model.parameter;

import org.springframework.data.rest.core.annotation.RepositoryRestResource;
import uk.gov.defra.datareturns.data.model.MasterDataRepository;
import uk.gov.defra.datareturns.data.projections.BasicAliasData;


/**
 * Spring REST repository for {@link Parameter} entities
 *
 * @author Sam Gardner-Dell
 */
@RepositoryRestResource(excerptProjection = BasicAliasData.class)
public interface ParameterRepository extends MasterDataRepository<Parameter> {
}
