package uk.gov.ea.datareturns.testsupport;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.ComponentScan;
import uk.gov.ea.datareturns.App;
import uk.gov.ea.datareturns.domain.jpa.dao.SiteDao;
import uk.gov.ea.datareturns.domain.jpa.dao.UniqueIdentifierAliasDao;
import uk.gov.ea.datareturns.domain.jpa.dao.UniqueIdentifierDao;
import uk.gov.ea.datareturns.domain.jpa.entities.Site;
import uk.gov.ea.datareturns.domain.jpa.entities.UniqueIdentifier;
import uk.gov.ea.datareturns.domain.jpa.entities.UniqueIdentifierAlias;

/**
 * @author Graham Willis
 * Once run initialization - remove the test permits and sites from the production
 * system. To be called from Jenkins
 */
@ComponentScan
@EnableAutoConfiguration
public class Initialization {
    protected static final Logger LOGGER = LoggerFactory.getLogger(Initialization.class);

    public static void main(String[] args) {
        ConfigurableApplicationContext ac = SpringApplication.run(App.class, args);
        LOGGER.info("One off initialization to remove test data from production system...");
        SiteDao siteDao = ac.getBean(SiteDao.class);
        UniqueIdentifierDao uniqueIdentifierDao = ac.getBean(UniqueIdentifierDao.class);
        UniqueIdentifierAliasDao uniqueIdentifierAliasDao = ac.getBean(UniqueIdentifierAliasDao.class);

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

        // Close down the spring context
        ac.close();
    }
}
