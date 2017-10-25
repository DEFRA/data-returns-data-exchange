package uk.gov.ea.datareturns.domain.jpa.repositories.masterdata;

import org.springframework.stereotype.Repository;
import uk.gov.ea.datareturns.domain.jpa.entities.masterdata.impl.Site;
import uk.gov.ea.datareturns.domain.jpa.entities.masterdata.impl.UniqueIdentifier;

import java.util.List;

@Repository public interface UniqueIdentifierRepository extends MasterDataRepository<UniqueIdentifier> {
    List<UniqueIdentifier> findUniqueIdentifiersBySite(Site site);

    List<UniqueIdentifier> findUniqueIdentifiersBySiteName(String siteName);
}
