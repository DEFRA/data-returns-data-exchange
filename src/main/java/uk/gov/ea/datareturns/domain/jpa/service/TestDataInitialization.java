package uk.gov.ea.datareturns.domain.jpa.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Profile;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import uk.gov.ea.datareturns.domain.jpa.dao.SiteDao;
import uk.gov.ea.datareturns.domain.jpa.dao.UniqueIdentifierAliasDao;
import uk.gov.ea.datareturns.domain.jpa.dao.UniqueIdentifierDao;
import uk.gov.ea.datareturns.domain.jpa.entities.Site;
import uk.gov.ea.datareturns.domain.jpa.entities.UniqueIdentifier;
import uk.gov.ea.datareturns.domain.jpa.entities.UniqueIdentifierAlias;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.inject.Inject;

/**
 * @author Graham Willis
 * Test data initialization. Adds test data to the persistence layer
 * on start-up and removes on shutdown. Called wherever the TestData profile is used
 */
@Component
@Scope(ConfigurableBeanFactory.SCOPE_SINGLETON)
@Profile("TestData")
public class TestDataInitialization {
    protected static final Logger LOGGER = LoggerFactory.getLogger(TestDataInitialization.class);

    /*
     * The following test data is used
     */
    private static final String EA_ID_01 = "AA9999AA";
    private static final String EA_ID_02 = "AA9997AA";
    private static final String EA_ID_ALIAS = "AA9998AA";
    private static final String SITE_01 = "Test Site";
    private static final String SITE_02 = "Test Site B";
    private final Search search;

    private UniqueIdentifierDao uniqueIdentifierDao;
    private SiteDao siteDao;
    private UniqueIdentifierAliasDao uniqueIdentifierAliasDao;

    // Run this post startup in production only
    @Inject
    public TestDataInitialization(SiteDao siteDao,
                                  UniqueIdentifierDao uniqueIdentifierDao,
                                  UniqueIdentifierAliasDao uniqueIdentifierAliasDao,
                                  Search search) {

        this.uniqueIdentifierDao = uniqueIdentifierDao;
        this.siteDao = siteDao;
        this.uniqueIdentifierAliasDao = uniqueIdentifierAliasDao;
        this.search = search;

    }

    @PostConstruct
    public void setUpTestData() {
        LOGGER.info("Test data initialization...");

        // Remove any test data still persisted
        // probably due to an ungraceful termination
        tearDownTestData();

        // Add in the test data
        try {
            Site site2 = new Site();
            site2.setName(SITE_02);

            Site site1 = new Site();
            site1.setName(SITE_01);

            UniqueIdentifier uniqueIdentifier2 = new UniqueIdentifier();
            uniqueIdentifier2.setName(EA_ID_02);
            uniqueIdentifier2.setSite(site2);

            UniqueIdentifier uniqueIdentifier1 = new UniqueIdentifier();
            uniqueIdentifier1.setName(EA_ID_01);
            uniqueIdentifier1.setSite(site1);

            UniqueIdentifierAlias uniqueIdentifierAlias = new UniqueIdentifierAlias();
            uniqueIdentifierAlias.setName(EA_ID_ALIAS);
            uniqueIdentifierAlias.setUniqueIdentifier(uniqueIdentifier1);

            siteDao.add(site2);
            siteDao.add(site1);
            uniqueIdentifierDao.add(uniqueIdentifier2);
            uniqueIdentifierDao.add(uniqueIdentifier1);
            uniqueIdentifierAliasDao.add(uniqueIdentifierAlias);

        } catch (Exception e) {
            LOGGER.warn("Error adding test data: " + e.getMessage());
        }

        search.initialize();

    }

    @PreDestroy
    public void tearDownTestData() {
        try {
            LOGGER.info("Test data removal...");

            short ctr = 0;

            UniqueIdentifierAlias uniqueIdentifierAlias = uniqueIdentifierAliasDao.getByName(EA_ID_ALIAS);

            UniqueIdentifier uniqueIdentifier1 = uniqueIdentifierDao.getByName(EA_ID_01);
            UniqueIdentifier uniqueIdentifier2 = uniqueIdentifierDao.getByName(EA_ID_02);

            Site site1 = siteDao.getByName(SITE_01);
            Site site2 = siteDao.getByName(SITE_02);

            // Delete the alias
            if (uniqueIdentifierAlias != null) {
                uniqueIdentifierAliasDao.removeById(uniqueIdentifierAlias.getId());
                ctr++;
            }

            // Delete the two permits
            if (uniqueIdentifier1 != null) {
                uniqueIdentifierDao.removeById(uniqueIdentifier1.getId());
                ctr++;
            }

            if (uniqueIdentifier2 != null) {
                uniqueIdentifierDao.removeById(uniqueIdentifier2.getId());
                ctr++;
            }

            // Delete the two sites
            if (site1 != null) {
                siteDao.removeById(site1.getId());
                ctr++;
            }

            if (site2 != null) {
                siteDao.removeById(site2.getId());
                ctr++;
            }

            LOGGER.info(ctr + " records removed");
        } catch (Exception e) {
            LOGGER.info("Cannot remove test data: " + e.getMessage());
        }
    }
}
