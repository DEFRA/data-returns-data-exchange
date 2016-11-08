package uk.gov.ea.datareturns.tests.domain.model;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
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
@SpringBootTest(classes=App.class)
@DirtiesContext
@RunWith(SpringRunner.class)
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
        //for (UniqueIdentifier id : list) {
        //    System.out.println(id);
        //}
    }

    @Test
    public void listSites() {
        List<Site> list = siteDao.list();
        Assert.assertNotNull(list);
        for (Site site : list) {
            System.out.println(site);
        }
    }


    @Test
    public void siteSearch() {
        search.searchSite("Lindey Oil");
        Assert.assertEquals(1, 1);
    }

}
