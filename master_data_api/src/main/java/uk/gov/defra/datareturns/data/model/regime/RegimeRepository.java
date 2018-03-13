package uk.gov.defra.datareturns.data.model.regime;

import org.springframework.data.repository.query.Param;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;
import org.springframework.data.rest.core.annotation.RestResource;
import uk.gov.defra.datareturns.data.model.Context;
import uk.gov.defra.datareturns.data.model.MasterDataRepository;
import uk.gov.defra.datareturns.data.model.eaid.UniqueIdentifier;
import uk.gov.defra.datareturns.data.model.regimeobligation.RegimeObligation;

import java.util.List;


/**
 * Spring REST repository for {@link Regime} entities
 *
 * @author Sam Gardner-Dell
 */
@RepositoryRestResource
public interface RegimeRepository extends MasterDataRepository<Regime> {
    @RestResource(path = "findRegimesForUniqueIdentifier")
    List<Regime> findRegimesByUniqueIdentifiersContains(@Param("id") UniqueIdentifier identifier);


    @RestResource(path = "findRegimesForContextAndUniqueIdentifier")
    List<Regime> findRegimesByContextIsAndUniqueIdentifiersContains(@Param("context") Context context,
                                                                    @Param("id") UniqueIdentifier identifier);
}
