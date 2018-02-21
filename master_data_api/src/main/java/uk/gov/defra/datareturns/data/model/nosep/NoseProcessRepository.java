package uk.gov.defra.datareturns.data.model.nosep;

import org.springframework.data.rest.core.annotation.RepositoryRestResource;
import uk.gov.defra.datareturns.data.model.MasterDataRepository;

@RepositoryRestResource
public interface NoseProcessRepository extends MasterDataRepository<NoseProcess>  {
}
