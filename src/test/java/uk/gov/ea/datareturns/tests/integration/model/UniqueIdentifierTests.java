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
import java.util.Arrays;
import java.util.HashSet;
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
        UniqueIdentifier uniqueIdentifier = uniqueIdentifierDao.getByName("ZP3933LD");
        Assert.assertEquals(uniqueIdentifier.getName(), "ZP3933LD");
        uniqueIdentifier = uniqueIdentifierDao.getByName("BL9500IJ");
        Assert.assertEquals(uniqueIdentifier.getName(), "BL9500IJ");
    }

    /**
     * Test the retrieval of a UniqueIdentifier from its alias name
     */
    @Test
    public void getUniqueIdentifierFromAliasName() {
        UniqueIdentifier uniqueIdentifier = uniqueIdentifierDao.getByNameOrAlias(Key.explicit("KP3030NG"));
        Assert.assertEquals(uniqueIdentifier.getName(), "BS7722ID");
        uniqueIdentifier = uniqueIdentifierDao.getByNameOrAlias(Key.relaxed("JB3937RN"));
        Assert.assertEquals(uniqueIdentifier.getName(), "104554");
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
        boolean found = uniqueIdentifierDao.uniqueIdentifierExists("KP3030NG");
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
     * Get a site - the site names are exact - we may not use a relaxed name
     */
    @Test
    public void getSite() {
        Site s1 = siteDao.getByName("Land North Of The Sewage Works");
        Assert.assertNotNull(s1);
        Site s2 = siteDao.getByName(Key.relaxed("Land  North Of   The Sewage  WORKS"));
        Assert.assertNull(s2);
    }

    /**
     * Permit set used for tests involving aliases
     */
    private static Set<String> permitSet = new HashSet<String>() {{
        add("YP3638SX");
        add("XP3732XP");
        add("NP3935DM");
        add("FP3935GQ");
        add("ZP3134NK");
    }};

    /**
     * Get the set of names from a given unique identifier name
     */
    @Test
    public void getNamesFromUniqueIdentifierName() {
        Set<String> names = uniqueIdentifierDao.getAllUniqueIdentifierNames("YP3638SX");
        Assert.assertTrue(names.containsAll(permitSet) && permitSet.containsAll(names));
    }

    /**
     * Get the set of names from a given alias identifier name
     */
    @Test
    public void getNamesFromUniqueIdentifierAliasName() {
        Set<String> names = uniqueIdentifierDao.getAllUniqueIdentifierNames("ZP3134NK");
        Assert.assertTrue(names.containsAll(permitSet) && permitSet.containsAll(names));
    }

    /**
     * Test the site name cache
     */
    @Test
    public void uniqueIdentifierBySiteName() {
        Set<UniqueIdentifier> uniqueIdentifiers = uniqueIdentifierDao.getUniqueIdentifierBySiteName("CANDLES LANDFILL");
        Assert.assertNotNull(uniqueIdentifiers);
        Set<String> names = uniqueIdentifiers.stream().map(UniqueIdentifier::getName).collect(Collectors.toSet());
        Assert.assertTrue(names.containsAll(Arrays.asList("BU9084IJ", "KP3238PU")));
    }
}