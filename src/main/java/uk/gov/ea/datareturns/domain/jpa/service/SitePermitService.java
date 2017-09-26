package uk.gov.ea.datareturns.domain.jpa.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionDefinition;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.DefaultTransactionDefinition;
import uk.gov.ea.datareturns.domain.jpa.entities.masterdata.impl.Site;
import uk.gov.ea.datareturns.domain.jpa.entities.masterdata.impl.UniqueIdentifier;
import uk.gov.ea.datareturns.domain.jpa.entities.masterdata.impl.UniqueIdentifierAlias;
import uk.gov.ea.datareturns.domain.jpa.repositories.masterdata.SiteRepository;
import uk.gov.ea.datareturns.domain.jpa.repositories.masterdata.UniqueIdentifierRepository;

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
    private final PlatformTransactionManager transactionManager;

    @Inject
    public SitePermitService(SiteRepository siteRepository,
            UniqueIdentifierRepository uniqueIdentifierRepository,
            PlatformTransactionManager transactionManager) {

        this.uniqueIdentifierRepository = uniqueIdentifierRepository;
        this.siteRepository = siteRepository;
        this.transactionManager = transactionManager;
    }

    /**
     * Add a new permit site and associated aliases
     * Any exception occurring in this function will cause the transaction to be rolled back
     *
     * @param eaId
     * @param siteName
     * @param aliasNames
     */
    @Transactional(propagation = Propagation.NESTED)
    public void addNewPermitAndSite(String eaId, String siteName, String[] aliasNames) {

        // Acquire a distributed lock on all remote servers for this transaction

        //        DefaultTransactionDefinition def = new DefaultTransactionDefinition();
        //        def.setName("addNewPermitAndSite");
        //        def.setPropagationBehavior(TransactionDefinition.PROPAGATION_NESTED);
        //
        //        TransactionStatus status = transactionManager.getTransaction(def);

        Site site = new Site();
        site.setName(siteName);
        siteRepository.save(site);

        UniqueIdentifier uniqueIdentifier = new UniqueIdentifier();
        uniqueIdentifier.setName(eaId);
        uniqueIdentifier.setSite(site);

        Set<UniqueIdentifierAlias> aliases = Arrays.stream(aliasNames)
                .map(aliasName -> {
                    UniqueIdentifierAlias alias = new UniqueIdentifierAlias();
                    alias.setName(aliasName);
                    alias.setPreferred(uniqueIdentifier);
                    return alias;
                })
                .collect(Collectors.toSet());
        uniqueIdentifier.setAliases(aliases);

        uniqueIdentifierRepository.saveAndFlush(uniqueIdentifier);

        // This can cause an exception on write to the database so that transaction is rolled back the
        // proceeding cache clear functions are not called.
        //        transactionManager.commit(status);
    }

    /**
     * Add a new permit and site without aliases. Clears the cache
     * @param eaId
     * @param siteName
     */
    public void addNewPermitAndSite(String eaId, String siteName) {

        // Acquire a distributed lock on all remote servers for this transaction

        DefaultTransactionDefinition def = new DefaultTransactionDefinition();
        def.setName("addNewPermitAndSite");
        def.setPropagationBehavior(TransactionDefinition.PROPAGATION_NESTED);

        TransactionStatus status = transactionManager.getTransaction(def);

        Site site = new Site();
        site.setName(siteName);

        UniqueIdentifier uniqueIdentifier = new UniqueIdentifier();
        uniqueIdentifier.setName(eaId);
        uniqueIdentifier.setSite(site);

        siteRepository.save(site);
        uniqueIdentifierRepository.save(uniqueIdentifier);

        // Commit the database transaction
        transactionManager.commit(status);

    }

    /**
     * Remove the aliases, site and ea_id associated with an ea_id
     * @param eaId
     */
    @Transactional(propagation = Propagation.NESTED)
    public void removePermitSiteAndAliases(String eaId) {
        UniqueIdentifier uniqueIdentifier = uniqueIdentifierRepository.getByName(eaId);

        if (uniqueIdentifier != null) {

            // Acquire a distributed lock on all remote servers for this transaction
            //
            //            DefaultTransactionDefinition def = new DefaultTransactionDefinition();
            //            def.setName("removePermitSiteAndAliases");
            //            def.setPropagationBehavior(TransactionDefinition.PROPAGATION_NESTED);
            //
            //            TransactionStatus status = transactionManager.getTransaction(def);

            Site site = uniqueIdentifier.getSite();
            uniqueIdentifierRepository.delete(uniqueIdentifier.getId());
            siteRepository.delete(site.getId());
            //
            //            // Commit the database transaction
            //            transactionManager.commit(status);

        } else {
            LOGGER.warn("Requested removal of non-existent permit: " + eaId);
        }
    }
}
