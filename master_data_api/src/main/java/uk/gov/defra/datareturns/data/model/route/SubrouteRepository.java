package uk.gov.defra.datareturns.data.model.route;

import org.springframework.data.rest.core.annotation.RepositoryRestResource;
import uk.gov.defra.datareturns.data.model.MasterDataRepository;


/**
 * Spring REST repository for {@link Subroute} entities
 *
 * @author Sam Gardner-Dell
 */
@RepositoryRestResource
public interface SubrouteRepository extends MasterDataRepository<Subroute> {
}
