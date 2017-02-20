package uk.gov.ea.datareturns.domain.jpa.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;
import uk.gov.ea.datareturns.domain.jpa.dao.SiteDao;
import uk.gov.ea.datareturns.domain.jpa.dao.UniqueIdentifierAliasDao;
import uk.gov.ea.datareturns.domain.jpa.dao.UniqueIdentifierDao;
import uk.gov.ea.datareturns.domain.jpa.entities.Site;
import uk.gov.ea.datareturns.domain.jpa.entities.UniqueIdentifier;
import uk.gov.ea.datareturns.domain.jpa.entities.UniqueIdentifierAlias;

import javax.annotation.PostConstruct;
import javax.inject.Inject;

/**
 * @author Graham Willis
 * Once run initialization - remove the test permits and sites from the production
 * system. To be called from Jenkins
 */
@Component
@Profile("production")
public class DataInitialization {
    protected static final Logger LOGGER = LoggerFactory.getLogger(DataInitialization.class);

    private UniqueIdentifierDao uniqueIdentifierDao;
    private SiteDao siteDao;
    private UniqueIdentifierAliasDao uniqueIdentifierAliasDao;

    // Run this post startup in production only
    @Inject
    public DataInitialization(SiteDao siteDao,
                              UniqueIdentifierDao uniqueIdentifierDao,
                              UniqueIdentifierAliasDao uniqueIdentifierAliasDao) {

        this.uniqueIdentifierDao = uniqueIdentifierDao;
        this.siteDao = siteDao;
        this.uniqueIdentifierAliasDao = uniqueIdentifierAliasDao;

    }

    @PostConstruct
    public void doInitialization() {

        try {
            LOGGER.info("One off initialization to remove test data from production system...");
            UniqueIdentifierAlias uniqueIdentifierAlias = uniqueIdentifierAliasDao.getByName("AA9998AA");
            UniqueIdentifier uniqueIdentifier1 = uniqueIdentifierDao.getByName("AA9999AA");
            UniqueIdentifier uniqueIdentifier2 = uniqueIdentifierDao.getByName("AA9997AA");
            Site site1 = siteDao.getByName("Test Site");
            Site site2 = siteDao.getByName("Test Site B");

            // Delete the alias
            if (uniqueIdentifierAlias != null) {
                uniqueIdentifierAliasDao.removeById(uniqueIdentifierAlias.getId());
            }

            // Delete the two permits
            if (uniqueIdentifier1 != null) {
                uniqueIdentifierDao.removeById(uniqueIdentifier1.getId());
            }

            if (uniqueIdentifier2 != null) {
                uniqueIdentifierDao.removeById(uniqueIdentifier2.getId());
            }

            // Delete the two sites
            if (site1 != null) {
                siteDao.removeById(site1.getId());
            }

            if (site2 != null) {
                siteDao.removeById(site2.getId());
            }
        } catch (Exception e) {
            LOGGER.warn("Error removing test data");
        }

    }
}
