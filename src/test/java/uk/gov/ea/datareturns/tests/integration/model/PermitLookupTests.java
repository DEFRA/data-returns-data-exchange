package uk.gov.ea.datareturns.tests.integration.model;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.dao.DataAccessException;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;
import uk.gov.ea.datareturns.App;
import uk.gov.ea.datareturns.domain.dto.impl.PermitLookupDto;
import uk.gov.ea.datareturns.domain.jpa.service.SitePermitService;
import uk.gov.ea.datareturns.domain.processors.SearchProcessor;

import javax.inject.Inject;
import java.io.IOException;
import java.util.Set;

/**
 * Created by graham on 20/01/17.
 */
@RunWith(SpringRunner.class)
@SpringBootTest(classes = App.class, webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
@ActiveProfiles("IntegrationTests")
public class PermitLookupTests {

    protected static final Logger LOGGER = LoggerFactory.getLogger(PermitLookupTests.class);

    private static final String TEST_SITE_NAME = "TEST_SITE_NAME";
    private static final String UNIQUE_ID = "UNIQUE_ID";
    private static final String UNIQUE_ID_ALIAS = "UNIQUE_ID_ALIAS";

    @Inject private SitePermitService sitePermitService;
    @Inject private SearchProcessor searchProcessor;

    @Before
    public void initialize() throws IOException {
        try {
        LOGGER.info("Initialize tests");

        sitePermitService.removePermitSiteAndAliases(UNIQUE_ID);
        sitePermitService.addNewPermitAndSite(UNIQUE_ID, TEST_SITE_NAME, new String [] { UNIQUE_ID_ALIAS });

        } catch (DataAccessException e) {
            LOGGER.warn("Cannot add the test data: " + e.getMessage());
        }
    }

    @Test
    public void permitLookupByPermitNumber() {
        PermitLookupDto dto = searchProcessor.getBySiteOrPermit(UNIQUE_ID);
        testLookupResults(dto);
    }

    @Test
    public void permitLookupByAlias() {
        PermitLookupDto dto = searchProcessor.getBySiteOrPermit(UNIQUE_ID_ALIAS);
        testLookupResults(dto);
    }

    @Test
    public void permitLookupBySite() {
        PermitLookupDto dto = searchProcessor.getBySiteOrPermit(TEST_SITE_NAME);
        testLookupResults(dto);
    }

    private void testLookupResults(PermitLookupDto dto) {
        Set<PermitLookupDto.Results> results = dto.getResults();
        Assert.assertEquals(1, results.size());
        Assert.assertEquals(((PermitLookupDto.Results)results.toArray()[0]).getUniqueIdentifier().getSite().getName(), TEST_SITE_NAME);
        Assert.assertEquals(((PermitLookupDto.Results)results.toArray()[0]).getUniqueIdentifier().getName(), UNIQUE_ID);
        Set<String> alternatives = ((PermitLookupDto.Results) results.toArray()[0]).getAlternatives();
        Assert.assertEquals(alternatives.size(), 1);
        Assert.assertEquals(alternatives.toArray()[0], UNIQUE_ID_ALIAS);
    }

    @After
    public void cleanUp() {
        try {
            LOGGER.info("Cleanup tests");
            sitePermitService.removePermitSiteAndAliases(UNIQUE_ID);
        } catch (DataAccessException e) {
            LOGGER.warn("Cannot remove the test data: " + e.getMessage());
        }
    }
}
