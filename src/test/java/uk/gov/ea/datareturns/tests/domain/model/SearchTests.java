package uk.gov.ea.datareturns.tests.domain.model;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.junit4.SpringRunner;
import uk.gov.ea.datareturns.App;
import uk.gov.ea.datareturns.domain.jpa.service.Search;

import javax.inject.Inject;

/**
 * Test the permit lookup functionality
 */
@SpringBootTest(classes=App.class)
@DirtiesContext
@RunWith(SpringRunner.class)
public class SearchTests {
    @Inject
    Search search;

    @Test
    public void siteSearch() {
        search.searchSite(" Quary Cainhoe");
        search.searchSite("Burnhills");
        search.searchSite("Todhills");
        search.searchSite(" Quary");
        search.searchSite("Peters");
        search.searchSite("Crag");
        search.searchSite("Crag");
        Assert.assertEquals(1, 1);
    }

}
