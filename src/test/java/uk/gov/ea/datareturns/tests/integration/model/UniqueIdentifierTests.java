package uk.gov.ea.datareturns.tests.integration.model;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;
import uk.gov.ea.datareturns.App;
import uk.gov.ea.datareturns.domain.jpa.dao.Key;
import uk.gov.ea.datareturns.domain.jpa.dao.SiteDao;
import uk.gov.ea.datareturns.domain.jpa.dao.UniqueIdentifierDao;
import uk.gov.ea.datareturns.domain.jpa.entities.Site;
import uk.gov.ea.datareturns.domain.jpa.entities.UniqueIdentifier;

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
        UniqueIdentifier uniqueIdentifier = uniqueIdentifierDao.getByName("AA9999AA");
        Assert.assertEquals(uniqueIdentifier.getName(), "AA9999AA");
        uniqueIdentifier = uniqueIdentifierDao.getByName("AA9999AA");
        Assert.assertEquals(uniqueIdentifier.getName(), "AA9999AA");
    }

    /**
     * Test the retrieval of a UniqueIdentifier from its alias name
     */
    @Test
    public void getUniqueIdentifierFromAliasName() {
        UniqueIdentifier uniqueIdentifier = uniqueIdentifierDao.getByNameOrAlias(Key.explicit("AA9998AA"));
        Assert.assertEquals(uniqueIdentifier.getName(), "AA9999AA");
        uniqueIdentifier = uniqueIdentifierDao.getByNameOrAlias(Key.relaxed("AA9998AA"));
        Assert.assertEquals(uniqueIdentifier.getName(), "AA9999AA");
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
        boolean found = uniqueIdentifierDao.uniqueIdentifierExists("AA9998AA");
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
        Site s1 = siteDao.getByName("Test Site");
        Assert.assertNotNull(s1);
        Site s2 = siteDao.getByName(Key.relaxed(" TEST   sitE "));
        Assert.assertNull(s2);
    }

    /**
     * Test the site name cache
     */
    @Test
    public void uniqueIdentifierBySiteName() {
        Set<UniqueIdentifier> uniqueIdentifiers = uniqueIdentifierDao.getUniqueIdentifierBySiteName("Test Site");
        Assert.assertNotNull(uniqueIdentifiers);
        Set<String> names = uniqueIdentifiers.stream().map(UniqueIdentifier::getName).collect(Collectors.toSet());
        Assert.assertTrue(names.contains("AA9999AA"));
    }
}