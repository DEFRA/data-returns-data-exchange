package uk.gov.ea.datareturns.domain.jpa.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import uk.gov.ea.datareturns.domain.jpa.entities.masterdata.impl.Site;
import uk.gov.ea.datareturns.domain.jpa.entities.masterdata.impl.UniqueIdentifier;
import uk.gov.ea.datareturns.domain.jpa.entities.masterdata.impl.UniqueIdentifierAlias;
import uk.gov.ea.datareturns.domain.jpa.entities.userdata.impl.DatasetCollection;
import uk.gov.ea.datareturns.domain.jpa.repositories.masterdata.SiteRepository;
import uk.gov.ea.datareturns.domain.jpa.repositories.masterdata.UniqueIdentifierRepository;
import uk.gov.ea.datareturns.domain.jpa.repositories.userdata.DatasetCollectionRepository;
import uk.gov.ea.datareturns.domain.jpa.repositories.userdata.DatasetRepository;

import javax.inject.Inject;
import java.util.Arrays;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * @author Graham Willis
 * Transactional service layer to perform common operations on the site and permit data
 *
 * This particular service does not use declarative transactions becuase of the need to
 * re-build the lucene index only on a sucessful commit and only when all the atomic updates have completed.
 *
 * An exception may be thrown by the commit which throws out to the caller
 */
@Service
public class SitePermitService {
    protected static final Logger LOGGER = LoggerFactory.getLogger(SitePermitService.class);

    private final UniqueIdentifierRepository uniqueIdentifierRepository;
    private final SiteRepository siteRepository;
    private final DatasetCollectionRepository datasetCollectionRepository;
    private final DatasetRepository datasetRepository;

    public class SitePermitServiceException extends Exception {
        public SitePermitServiceException(String message) {
            super(message);
        }
    }

    @Inject
    public SitePermitService(SiteRepository siteRepository,
            UniqueIdentifierRepository uniqueIdentifierRepository,
            DatasetCollectionRepository datasetCollectionRepository,
            DatasetRepository datasetRepository) {
        this.uniqueIdentifierRepository = uniqueIdentifierRepository;
        this.siteRepository = siteRepository;
        this.datasetCollectionRepository = datasetCollectionRepository;
        this.datasetRepository = datasetRepository;
    }

    // Gets the extended graph from the cache
    public UniqueIdentifier getUniqueIdentifierByName(String eaIdId) {
        return uniqueIdentifierRepository.getByName(eaIdId);
    }

    /**
     * Add a new permit site and associated aliases
     * Any exception occurring in this function will cause the transaction to be rolled back
     *
     * @param eaId
     * @param siteName
     * @param aliasNames
     */
    @Transactional
    public void addNewPermitAndSite(String eaId, String siteName,
            String[] aliasNames) {
        Site site = new Site();
        site.setName(siteName);

        UniqueIdentifier uniqueIdentifier = new UniqueIdentifier();
        uniqueIdentifier.setName(eaId);
        uniqueIdentifier.setSite(site);

        if (aliasNames != null) {
            Set<UniqueIdentifierAlias> aliases = Arrays.stream(aliasNames)
                    .map(aliasName -> {
                        UniqueIdentifierAlias alias = new UniqueIdentifierAlias();
                        alias.setName(aliasName);
                        alias.setPreferred(uniqueIdentifier);
                        return alias;
                    })
                    .collect(Collectors.toSet());
            uniqueIdentifier.setAliases(aliases);
        }
        siteRepository.save(site);
        uniqueIdentifierRepository.save(uniqueIdentifier);
    }

    /**
     * Add a new permit and site without aliases. Clears the cache
     * @param eaId
     * @param siteName
     */

    @Transactional
    public void addNewPermitAndSite(String eaId, String siteName) throws SitePermitServiceException {
        addNewPermitAndSite(eaId, siteName, null);
    }

    /**
     * Remove the aliases, site and ea_id associated with an ea_id
     * @param eaId
     */
    @Transactional
    public void removePermitSiteAndAliases(String eaId) {
        UniqueIdentifier uniqueIdentifier = uniqueIdentifierRepository.getByName(eaId);
        if (uniqueIdentifier != null) {
            // Remove any data associatted with the unique identifier
            DatasetCollection collection = datasetCollectionRepository.getByUniqueIdentifier(uniqueIdentifier);
            if (collection != null) {
//                datasetRepository.delete(collection.getDatasets());

                datasetCollectionRepository.delete(collection);
            }

//            Site site = uniqueIdentifier.getSite();
//            siteRepository.delete(site.getId());

            uniqueIdentifierRepository.delete(uniqueIdentifier.getId());
        } else {
            LOGGER.warn("Requested removal of non-existent permit: " + eaId);
        }
    }
}
