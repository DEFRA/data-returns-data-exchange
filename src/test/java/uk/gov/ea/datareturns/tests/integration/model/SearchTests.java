package uk.gov.ea.datareturns.tests.integration.model;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;
import uk.gov.ea.datareturns.App;
import uk.gov.ea.datareturns.domain.jpa.dao.SiteDao;
import uk.gov.ea.datareturns.domain.jpa.dao.UniqueIdentifierAliasDao;
import uk.gov.ea.datareturns.domain.jpa.dao.UniqueIdentifierDao;
import uk.gov.ea.datareturns.domain.jpa.entities.Site;
import uk.gov.ea.datareturns.domain.jpa.entities.UniqueIdentifier;
import uk.gov.ea.datareturns.domain.jpa.service.Search;

import javax.inject.Inject;
import java.util.List;

/**
 * Test the permit lookup functionality
 */
@RunWith(SpringRunner.class)
@SpringBootTest(classes = App.class, webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
@ActiveProfiles("IntegrationTests")
public class SearchTests {
    @Inject
    UniqueIdentifierDao uniqueIdentifierDao;

    @Inject
    UniqueIdentifierAliasDao uniqueIdentifierAliasDao;

    @Inject
    SiteDao siteDao;

    @Inject
    Search search;

    @Test
    public void listUniqueIdentifiers() {
        List<UniqueIdentifier> list = uniqueIdentifierDao.list();
        Assert.assertNotNull(list);
    }

    @Test
    public void listSites() {
        List<Site> list = siteDao.list();
        Assert.assertNotNull(list);
    }

    @Test
    public void siteSearch() {
        search.searchSite("Aycliffe Quarry");
        search.searchSite("Weights Landfill Farm ");
        search.searchSite("Aycliffe Quary");
        search.searchSite("Weight Landfill Farm ");
        search.searchSite("Aycliffe");
        search.searchSite("Weight Landfill");
        Assert.assertEquals(1, 1);

        // TODO: Graham - this test isn't doing anything....
    }

}
