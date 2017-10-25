package uk.gov.ea.datareturns.tests.integration.model;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;
import uk.gov.ea.datareturns.App;
import uk.gov.ea.datareturns.domain.jpa.entities.masterdata.impl.Site;
import uk.gov.ea.datareturns.domain.jpa.entities.masterdata.impl.UniqueIdentifier;
import uk.gov.ea.datareturns.domain.jpa.entities.masterdata.impl.UniqueIdentifierAlias;
import uk.gov.ea.datareturns.domain.jpa.repositories.masterdata.SiteRepository;
import uk.gov.ea.datareturns.domain.jpa.repositories.masterdata.UniqueIdentifierAliasRepository;
import uk.gov.ea.datareturns.domain.jpa.repositories.masterdata.UniqueIdentifierRepository;
import uk.gov.ea.datareturns.domain.jpa.service.MasterDataLookupService;

import javax.inject.Inject;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Created by graham on 08/11/16.
 */
@RunWith(SpringRunner.class)
@SpringBootTest(classes = App.class, webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
@ActiveProfiles("IntegrationTests")
public class UniqueIdentifierTests {
    @Inject
    SiteRepository siteRepository;

    @Inject
    UniqueIdentifierRepository uniqueIdentifierRepository;

    @Inject
    UniqueIdentifierAliasRepository uniqueIdentifierAliasRepository;

    @Inject
    MasterDataLookupService lookupService;

    /**
     * Test the retrieval of a UniqueIdentifier from its name.
     * Also tests a second retrieval from teh name cache
     */
    @Test
    public void getUniqueIdentifierFromName() {
        UniqueIdentifier uniqueIdentifier = uniqueIdentifierRepository.getByName("42355");
        Assert.assertEquals(uniqueIdentifier.getName(), "42355");
    }

    /**
     * Test the retrieval of a UniqueIdentifier from its alias name
     */
    @Test
    public void getUniqueIdentifierFromAliasName() {
        UniqueIdentifier uniqueIdentifier = uniqueIdentifierAliasRepository.getByName("UP3791FG").getPreferred();
        Assert.assertEquals("42355", uniqueIdentifier.getName());
        uniqueIdentifier = lookupService.relaxed().find(UniqueIdentifierAlias.class, "UP3791FG").getPreferred();
        Assert.assertEquals("42355", uniqueIdentifier.getName());
    }

    /**
     * Null test for a not found alias or ID
     */
    @Test
    public void getNullUniqueIdentifier() {
        UniqueIdentifier uniqueIdentifier = uniqueIdentifierRepository.getByName("jdghasfcighwfv");
        Assert.assertNull(uniqueIdentifier);
    }

    /**
     * Get a site
     */
    @Test
    public void getSite() {
        Site s1 = siteRepository.getByName("Biffa - Marchington Landfill Site");
        Assert.assertNotNull(s1);
        Site s2 = lookupService.relaxed().find(Site.class, " Biffa - marchington  Landfill Site");
        Assert.assertNull(s2);
    }

    /**
     * Test the site name cache
     */
    @Test
    public void uniqueIdentifierBySiteName() {
        List<UniqueIdentifier> uniqueIdentifiers = uniqueIdentifierRepository.findUniqueIdentifiersBySiteName("Biffa - Marchington Landfill Site");
        Assert.assertNotNull(uniqueIdentifiers);
        Set<String> names = uniqueIdentifiers.stream().map(UniqueIdentifier::getName).collect(Collectors.toSet());
        Assert.assertTrue(names.contains("42355"));
    }
}