package uk.gov.ea.datareturns.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DataAccessException;
import org.springframework.stereotype.Component;
import uk.gov.ea.datareturns.domain.jpa.service.SitePermitService;

import javax.inject.Inject;

/**
 * @author Graham Willis
 * Test data initialization. Adds test data to the persistence layer
 * on start-up and removes on shutdown. Called wherever the TestData profile is used
 */
@Component
public class TestDataInitialization {
    protected static final Logger LOGGER = LoggerFactory.getLogger(TestDataInitialization.class);

    private final SitePermitService sitePermitService;

    /*
     * The following test data is used
     */
    private static final String EA_ID_01 = "AA9999AA";
    private static final String EA_ID_02 = "AA9997AA";
    private static final String EA_ID_ALIAS = "AA9998AA";
    private static final String SITE_01 = "Test Site";
    private static final String SITE_02 = "Test Site B";

    @Inject
    public TestDataInitialization(SitePermitService sitePermitService) {
        this.sitePermitService = sitePermitService;
    }

    public void setUpTestData() {
        try {
            LOGGER.info("Test data initialization...");
            sitePermitService.addNewPermitAndSite(EA_ID_01, SITE_01, new String [] {EA_ID_ALIAS});
            sitePermitService.addNewPermitAndSite(EA_ID_02, SITE_02);
        } catch (DataAccessException e) {
            LOGGER.warn("Cannot add the test data: " + e.getMessage());
        }
    }

    public void tearDownTestData() {
        try {
            LOGGER.info("Test data removal...");
            sitePermitService.removePermitSiteAndAliases(EA_ID_01);
            sitePermitService.removePermitSiteAndAliases(EA_ID_02);
        } catch (DataAccessException e) {
            LOGGER.warn("Unable to remove the test data: " + e.getMessage());
        }
    }
}


