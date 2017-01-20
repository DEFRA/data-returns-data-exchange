package uk.gov.ea.datareturns.tests.integration.model;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;
import uk.gov.ea.datareturns.App;
import uk.gov.ea.datareturns.domain.dto.PermitLookupDto;
import uk.gov.ea.datareturns.domain.jpa.dao.SiteDao;
import uk.gov.ea.datareturns.domain.jpa.dao.UniqueIdentifierAliasDao;
import uk.gov.ea.datareturns.domain.jpa.dao.UniqueIdentifierDao;
import uk.gov.ea.datareturns.domain.jpa.entities.Site;
import uk.gov.ea.datareturns.domain.jpa.entities.UniqueIdentifier;
import uk.gov.ea.datareturns.domain.jpa.entities.UniqueIdentifierAlias;
import uk.gov.ea.datareturns.domain.jpa.service.Search;
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

    private static final String TEST_SITE_NAME = "TEST_SITE_NAME";
    private static final String UNIQUE_ID = "UNIQUE_ID";
    private static final String UNIQUE_ID_ALIAS = "UNIQUE_ID_ALIAS";

    @Inject private SiteDao siteDao;
    @Inject private UniqueIdentifierDao uniqueIdentifierDao;
    @Inject private UniqueIdentifierAliasDao uniqueIdentifierAliasDao;
    private SearchProcessor searchProcessor;
    private Search search;

    @Before
    public void initialize() throws IOException {
        Site site = new Site();
        site.setName(TEST_SITE_NAME);

        siteDao.add(site);
        site = siteDao.getByName(TEST_SITE_NAME);

        UniqueIdentifier uniqueIdentifier = new UniqueIdentifier();
        uniqueIdentifier.setName(UNIQUE_ID);
        uniqueIdentifier.setSite(site);
        uniqueIdentifierDao.add(uniqueIdentifier);
        uniqueIdentifier = uniqueIdentifierDao.getByName(UNIQUE_ID);

        UniqueIdentifierAlias uniqueIdentifierAlias = new UniqueIdentifierAlias();
        uniqueIdentifierAlias.setName(UNIQUE_ID_ALIAS);
        uniqueIdentifierAlias.setUniqueIdentifier(uniqueIdentifier);
        uniqueIdentifierAliasDao.add(uniqueIdentifierAlias);

        search = new Search(siteDao);
        searchProcessor = new SearchProcessor(search, uniqueIdentifierDao, siteDao);
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
        Assert.assertEquals(results.size(), 1);
        Assert.assertEquals(((PermitLookupDto.Results)results.toArray()[0]).getUniqueIdentifier().getSite().getName(), TEST_SITE_NAME);
        Assert.assertEquals(((PermitLookupDto.Results)results.toArray()[0]).getUniqueIdentifier().getName(), UNIQUE_ID);
        Set<String> alternatives = ((PermitLookupDto.Results) results.toArray()[0]).getAlternatives();
        Assert.assertEquals(alternatives.size(), 1);
        Assert.assertEquals(alternatives.toArray()[0], UNIQUE_ID_ALIAS);
    }

    @After
    public void cleanUp() {
        Site site = siteDao.getByName(TEST_SITE_NAME);
        UniqueIdentifier uniqueIdentifier = uniqueIdentifierDao.getByName(UNIQUE_ID);
        UniqueIdentifierAlias uniqueIdentifierAlias = uniqueIdentifierAliasDao.getByName(UNIQUE_ID_ALIAS);

        uniqueIdentifierAliasDao.removeById(uniqueIdentifierAlias.getId());
        uniqueIdentifierDao.removeById(uniqueIdentifier.getId());
        siteDao.removeById(site.getId());

    }

}
