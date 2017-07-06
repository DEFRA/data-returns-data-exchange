package uk.gov.ea.datareturns.tests.integration.model;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;
import uk.gov.ea.datareturns.App;
import uk.gov.ea.datareturns.domain.jpa.dao.masterdata.*;
import uk.gov.ea.datareturns.domain.jpa.entities.masterdata.impl.*;

import javax.inject.Inject;
import java.util.List;

/**
 * Unit tests for the controlled list functionality
 */
@RunWith(SpringRunner.class)
@SpringBootTest(classes = App.class, webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
@ActiveProfiles("IntegrationTests")
public class ControlledListsTests {

    public static final String NAME = "Casespaces";
    public static final String NAME_MASH = "CasEspAces";

    @Inject
    MethodOrStandardDao methodOrStandardDao;

    @Inject
    ParameterDao parameterDao;

    @Inject
    QualifierDao qualifierDao;

    @Inject
    ReferencePeriodDao referencePeriodDao;

    @Inject
    ReturnTypeDao returnTypeDao;

    @Inject
    UnitDao unitDao;

    @Inject
    TextValueDao textValueDao;

    @Test
    public void testMethodOrStandard() {
        MethodOrStandard methodOrStandard0 = methodOrStandardDao.getByName(NAME);
        if (methodOrStandard0 != null) {
            methodOrStandardDao.removeById(methodOrStandard0.getId());
        }
        // Add and retrieve
        MethodOrStandard methodOrStandard = new MethodOrStandard();
        methodOrStandard.setName(NAME);
        methodOrStandardDao.add(methodOrStandard);
        methodOrStandardDao.clearCaches();

        MethodOrStandard retrieveMethodOrStandard = methodOrStandardDao.getByName(NAME);
        Assert.assertNotNull(retrieveMethodOrStandard.getId());

        MethodOrStandard retrieveMethodOrStandard2 = methodOrStandardDao.getById(retrieveMethodOrStandard.getId());
        Assert.assertNotNull(retrieveMethodOrStandard2);
        Assert.assertEquals(retrieveMethodOrStandard2.getId(), retrieveMethodOrStandard.getId());

        methodOrStandardDao.removeById(retrieveMethodOrStandard2.getId());
        methodOrStandardDao.clearCaches();

        MethodOrStandard retrieveMethodOrStandard3 = methodOrStandardDao.getById(retrieveMethodOrStandard.getId());
        Assert.assertNull(retrieveMethodOrStandard3);
    }

    @Test
    public void testParameter() {
        Parameter retrieveParameter0 = parameterDao.getByName(NAME);
        if (retrieveParameter0 != null) {
            parameterDao.removeById(retrieveParameter0.getId());
        }
        // Add and retrieve
        Parameter parameter = new Parameter();
        parameter.setName(NAME);
        parameterDao.add(parameter);
        parameterDao.clearCaches();

        Parameter retrieveParameter = parameterDao.getByName(NAME);
        Assert.assertNotNull(retrieveParameter.getId());

        Parameter retrieveParameter2 = parameterDao.getById(retrieveParameter.getId());
        Assert.assertNotNull(retrieveParameter2);
        Assert.assertEquals(retrieveParameter2.getId(), retrieveParameter.getId());

        // Test the case-insensitive cache
        Assert.assertTrue(parameterDao.nameExists(Key.relaxed(NAME_MASH)));
        Assert.assertEquals(NAME, parameterDao.getByName(Key.relaxed(NAME_MASH)).getName());

        // Second test to prove cache use
        Assert.assertTrue(parameterDao.nameExists(Key.relaxed(NAME_MASH)));
        Assert.assertEquals(NAME, parameterDao.getByName(Key.relaxed(NAME_MASH)).getName());

        parameterDao.removeById(retrieveParameter2.getId());
        parameterDao.clearCaches();

        Assert.assertFalse(parameterDao.nameExists(Key.relaxed(NAME_MASH)));
        Assert.assertNull(parameterDao.getByName(Key.relaxed(NAME_MASH)));

        Parameter retrieveParameter3 = parameterDao.getById(retrieveParameter.getId());
        Assert.assertNull(retrieveParameter3);
    }

    @Test
    public void testReturnType() {
        final String LANDFILL = "Landfill";
        // Make the test type is gone
        ReturnType retrieveReturnType0 = returnTypeDao.getByName(NAME);
        if (retrieveReturnType0 != null) {
            returnTypeDao.removeById(retrieveReturnType0.getId());
        }
        // Add and retrieve
        ReturnType returnType = new ReturnType();
        returnType.setName(NAME);
        returnType.setSector(LANDFILL);
        returnTypeDao.add(returnType);
        returnTypeDao.clearCaches();

        ReturnType retrieveReturnType = returnTypeDao.getByName(NAME);
        Assert.assertNotNull(retrieveReturnType.getId());

        ReturnType retrieveReturnType2 = returnTypeDao.getById(retrieveReturnType.getId());
        Assert.assertNotNull(retrieveReturnType2);
        Assert.assertEquals(retrieveReturnType2.getId(), retrieveReturnType.getId());

        // Test the case-insensitive cache
        Assert.assertTrue(returnTypeDao.nameExists(Key.relaxed(NAME_MASH)));
        Assert.assertEquals(NAME, returnTypeDao.getByName(Key.relaxed(NAME_MASH)).getName());

        // Second test to prove cache use
        Assert.assertTrue(returnTypeDao.nameExists(Key.relaxed(NAME_MASH)));
        Assert.assertEquals(NAME, returnTypeDao.getByName(Key.relaxed(NAME_MASH)).getName());

        // Remove it and try again
        returnTypeDao.removeById(retrieveReturnType2.getId());
        returnTypeDao.clearCaches();
        Assert.assertFalse(returnTypeDao.nameExists(Key.relaxed(NAME_MASH)));
        Assert.assertNull(returnTypeDao.getByName(Key.relaxed(NAME_MASH)));

        ReturnType retrieveReturnType3 = returnTypeDao.getById(retrieveReturnType.getId());
        Assert.assertNull(retrieveReturnType3);
    }

    @Test
    public void testUnit() {
        // Make the test type is gone
        Unit retrieveUnit0 = unitDao.getByName(NAME);
        if (retrieveUnit0 != null) {
            unitDao.removeById(retrieveUnit0.getId());
        }
        // Add and retrieve
        Unit unit = new Unit();
        unit.setName(NAME);
        unit.setDescription(NAME + NAME + NAME);
        unit.setType("Type");
        unit.setLongName(NAME + NAME);
        unit.setUnicode("UC");

        unitDao.add(unit);
        unitDao.clearCaches();

        Unit retrieveUnit = unitDao.getByName(NAME);
        Assert.assertNotNull(retrieveUnit.getId());
        Assert.assertEquals(retrieveUnit.getDescription(), NAME + NAME + NAME);
        Assert.assertEquals(retrieveUnit.getLongName(), NAME + NAME);
        Assert.assertEquals(retrieveUnit.getType(), "Type");
        Assert.assertEquals(retrieveUnit.getUnicode(), "UC");

        Unit retrieveUnit2 = unitDao.getById(retrieveUnit.getId());
        Assert.assertNotNull(retrieveUnit2);
        Assert.assertEquals(retrieveUnit2.getId(), retrieveUnit.getId());

        unitDao.removeById(retrieveUnit2.getId());
        unitDao.clearCaches();

        Unit retrieveUnit3 = unitDao.getById(retrieveUnit.getId());
        Assert.assertNull(retrieveUnit3);
    }

    @Test
    public void testQualifier() {
        // Make the test type is gone
        Qualifier retrieveQualifier0 = qualifierDao.getByName(NAME);
        if (retrieveQualifier0 != null) {
            qualifierDao.removeById(retrieveQualifier0.getId());
        }
        // Add and retrieve
        Qualifier Qualifier = new Qualifier();
        Qualifier.setName(NAME);
        Qualifier.setNotes(NAME);
        qualifierDao.add(Qualifier);
        qualifierDao.clearCaches();

        Qualifier retrieveQualifier = qualifierDao.getByName(NAME);
        Assert.assertNotNull(retrieveQualifier.getId());
        Qualifier retrieveQualifier2 = qualifierDao.getById(retrieveQualifier.getId());
        Assert.assertNotNull(retrieveQualifier2);
        Assert.assertEquals(retrieveQualifier2.getId(), retrieveQualifier.getId());
        qualifierDao.removeById(retrieveQualifier2.getId());
        qualifierDao.clearCaches();

        Qualifier retrieveQualifier3 = qualifierDao.getById(retrieveQualifier.getId());
        Assert.assertNull(retrieveQualifier3);
    }

    @Test
    public void testTextValues() {
        TextValue textValue0 = textValueDao.getByName(NAME);
        if (textValue0 != null) {
            qualifierDao.removeById(textValue0.getId());
        }
        // Add and retrieve
        TextValue TextValue = new TextValue();
        TextValue.setName(NAME);
        textValueDao.add(TextValue);
        textValueDao.clearCaches();

        TextValue retrieveTextValue = textValueDao.getByName(NAME);
        Assert.assertNotNull(retrieveTextValue.getId());

        TextValue retrieveTextValue2 = textValueDao.getById(retrieveTextValue.getId());
        Assert.assertNotNull(retrieveTextValue2);
        Assert.assertEquals(retrieveTextValue2.getId(), retrieveTextValue.getId());

        textValueDao.removeById(retrieveTextValue2.getId());
        textValueDao.clearCaches();

        TextValue retrieveTextValue3 = textValueDao.getById(retrieveTextValue.getId());
        Assert.assertNull(retrieveTextValue3);
    }

    @Test
    public void testAlias() {

        final String PRIMARY_1 = "Primary 1";
        final String PRIMARY_2 = "Primary 2";
        final String PRIMARY_3 = "Primary 3";

        final String PRIMARY_1_MASH = "PRimary 1";
        final String PRIMARY_2_MASH = "PrImary 2";
        final String PRIMARY_3_MASH = "PriMary 3";

        ReferencePeriod referencePeriod1 = referencePeriodDao.getByNameOrAlias(Key.explicit(PRIMARY_1));
        ReferencePeriod referencePeriod2 = referencePeriodDao.getByNameOrAlias(Key.explicit(PRIMARY_2));
        ReferencePeriod referencePeriod3 = referencePeriodDao.getByNameOrAlias(Key.explicit(PRIMARY_3));

        // Have to remove 2 & 3 first in order not to violates foreign key constraint fk_reference_periods
        if (referencePeriod2 != null) {
            referencePeriodDao.removeById(referencePeriod2.getId());
        }

        if (referencePeriod3 != null) {
            referencePeriodDao.removeById(referencePeriod3.getId());
        }

        if (referencePeriod1 != null) {
            referencePeriodDao.removeById(referencePeriod1.getId());
        }

        referencePeriodDao.clearCaches();

        referencePeriod1 = new ReferencePeriod();
        referencePeriod2 = new ReferencePeriod();
        referencePeriod3 = new ReferencePeriod();

        referencePeriod1.setName(PRIMARY_1);
        referencePeriod2.setName(PRIMARY_2);
        referencePeriod3.setName(PRIMARY_3);

        referencePeriod2.setPreferred(referencePeriod1.getName());
        referencePeriod3.setPreferred(referencePeriod1.getName());

        referencePeriodDao.add(referencePeriod1);
        referencePeriodDao.add(referencePeriod2);
        referencePeriodDao.add(referencePeriod3);

        referencePeriodDao.clearCaches();

        List list = referencePeriodDao.list();

        Assert.assertNotNull(list);
        Assert.assertFalse(list.isEmpty());

        Assert.assertEquals(PRIMARY_1, referencePeriodDao.getByName(PRIMARY_1).getName());

        Assert.assertEquals(PRIMARY_2, referencePeriodDao.getByAliasName(Key.explicit(PRIMARY_2)).getName());
        Assert.assertEquals(PRIMARY_1, referencePeriodDao.getPreferred(Key.explicit(PRIMARY_2)).getName());

        Assert.assertEquals(PRIMARY_3, referencePeriodDao.getByAliasName(Key.relaxed(PRIMARY_3)).getName());
        Assert.assertEquals(PRIMARY_1, referencePeriodDao.getPreferred(Key.relaxed(PRIMARY_3)).getName());

        Assert.assertNull(referencePeriodDao.getByAliasName(Key.relaxed(PRIMARY_1)));

        Assert.assertTrue(referencePeriodDao.nameOrAliasExists(Key.relaxed(PRIMARY_1_MASH)));
        Assert.assertEquals(PRIMARY_1, referencePeriodDao.getPreferred(Key.relaxed(PRIMARY_1_MASH)).getName());

        Assert.assertTrue(referencePeriodDao.nameOrAliasExists(Key.relaxed(PRIMARY_2_MASH)));
        Assert.assertEquals(PRIMARY_1, referencePeriodDao.getPreferred(Key.relaxed(PRIMARY_2_MASH)).getName());

        Assert.assertTrue(referencePeriodDao.nameOrAliasExists(Key.relaxed(PRIMARY_3_MASH)));
        Assert.assertEquals(PRIMARY_1, referencePeriodDao.getPreferred(Key.relaxed(PRIMARY_3_MASH)).getName());

        referencePeriodDao.removeById(referencePeriod2.getId());
        referencePeriodDao.removeById(referencePeriod3.getId());
        referencePeriodDao.removeById(referencePeriod1.getId());
        referencePeriodDao.clearCaches();
    }

}

