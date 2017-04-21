package uk.gov.ea.datareturns.tests.integration.model;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;
import uk.gov.ea.datareturns.App;
import uk.gov.ea.datareturns.domain.jpa.dao.masterdata.Key;
import uk.gov.ea.datareturns.domain.jpa.dao.masterdata.SiteDao;
import uk.gov.ea.datareturns.domain.jpa.dao.masterdata.UniqueIdentifierDao;
import uk.gov.ea.datareturns.domain.jpa.entities.masterdata.impl.Site;
import uk.gov.ea.datareturns.domain.jpa.entities.masterdata.impl.UniqueIdentifier;

import javax.inject.Inject;
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
    SiteDao siteDao;

    @Inject
    UniqueIdentifierDao uniqueIdentifierDao;

    /**
     * Test the retrieval of a UniqueIdentifier from its name.
     * Also tests a second retrieval from teh name cache
     */
    @Test
    public void getUniqueIdentifierFromName() {
        UniqueIdentifier uniqueIdentifier = uniqueIdentifierDao.getByName("42355");
        Assert.assertEquals(uniqueIdentifier.getName(), "42355");
        uniqueIdentifier = uniqueIdentifierDao.getByName("42355");
        Assert.assertEquals(uniqueIdentifier.getName(), "42355");
    }

    /**
     * Test the retrieval of a UniqueIdentifier from its alias name
     */
    @Test
    public void getUniqueIdentifierFromAliasName() {
        UniqueIdentifier uniqueIdentifier = uniqueIdentifierDao.getByNameOrAlias(Key.explicit("UP3791FG"));
        Assert.assertEquals(uniqueIdentifier.getName(), "42355");
        uniqueIdentifier = uniqueIdentifierDao.getByNameOrAlias(Key.relaxed("UP3791FG"));
        Assert.assertEquals(uniqueIdentifier.getName(), "42355");
    }

    /**
     * Null test for a not found alias or ID
     */
    @Test
    public void getNullUniqueIdentifier() {
        UniqueIdentifier uniqueIdentifier = uniqueIdentifierDao.getByName("jdghasfcighwfv");
        Assert.assertNull(uniqueIdentifier);
    }

    /**
     * Test the retrieval of a UniqueIdentifier from its alias name
     */
    @Test
    public void getUniqueIdentifierFound() {
        boolean found = uniqueIdentifierDao.uniqueIdentifierExists("UP3791FG");
        Assert.assertTrue(found);
    }

    /**
     * Null test for a not found alias or ID
     */
    @Test
    public void getUniqueIdentifierNotFound() {
        boolean found = uniqueIdentifierDao.uniqueIdentifierExists("jdghasfcighwfv");
        Assert.assertNotNull(found);
    }

    /**
     * Get a site
     */
    @Test
    public void getSite() {
        Site s1 = siteDao.getByName("Biffa - Marchington Landfill Site");
        Assert.assertNotNull(s1);
        Site s2 = siteDao.getByName(Key.relaxed(" Biffa - marchington  Landfill Site"));
        Assert.assertNull(s2);
    }

    /**
     * Test the site name cache
     */
    @Test
    public void uniqueIdentifierBySiteName() {
        Set<UniqueIdentifier> uniqueIdentifiers = uniqueIdentifierDao.getUniqueIdentifierBySiteName("Biffa - Marchington Landfill Site");
        Assert.assertNotNull(uniqueIdentifiers);
        Set<String> names = uniqueIdentifiers.stream().map(UniqueIdentifier::getName).collect(Collectors.toSet());
        Assert.assertTrue(names.contains("42355"));
    }
}