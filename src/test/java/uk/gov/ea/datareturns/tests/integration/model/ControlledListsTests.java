package uk.gov.ea.datareturns.tests.integration.model;

import org.apache.commons.collections4.IterableUtils;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;
import uk.gov.ea.datareturns.App;
import uk.gov.ea.datareturns.domain.jpa.entities.masterdata.MasterDataEntity;
import uk.gov.ea.datareturns.domain.jpa.entities.masterdata.impl.*;
import uk.gov.ea.datareturns.domain.jpa.repositories.masterdata.*;
import uk.gov.ea.datareturns.domain.jpa.service.MasterDataLookupService;

import javax.inject.Inject;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;

/**
 * Unit tests for the controlled list functionality
 */
@RunWith(SpringRunner.class)
@SpringBootTest(classes = App.class, webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
@ActiveProfiles("IntegrationTests")
public class ControlledListsTests {
    private static final Logger LOGGER = LoggerFactory.getLogger(ControlledListsTests.class);

    public static final String NAME = "Casespaces";
    public static final String NAME_MASH = "CasEspAces";

    @Inject
    MethodOrStandardRepository methodOrStandardRepository;

    @Inject
    ParameterRepository parameterRepository;

    @Inject
    QualifierRepository qualifierRepository;

    @Inject
    ReferencePeriodRepository referencePeriodRepository;

    @Inject
    ReturnTypeRepository returnTypeRepository;

    @Inject
    UnitRepository unitRepository;

    @Inject
    TextValueRepository textValueRepository;

    @Inject
    MasterDataLookupService lookupService;

    @Test
    public void testMethodOrStandard() {
        MethodOrStandard methodOrStandard0 = methodOrStandardRepository.getByName(NAME);
        if (methodOrStandard0 != null) {
            methodOrStandardRepository.delete(methodOrStandard0.getId());
        }
        // Add and retrieve
        MethodOrStandard methodOrStandard = new MethodOrStandard();
        methodOrStandard.setName(NAME);
        methodOrStandardRepository.save(methodOrStandard);

        MethodOrStandard retrieveMethodOrStandard = methodOrStandardRepository.getByName(NAME);
        Assert.assertNotNull(retrieveMethodOrStandard.getName());

        MethodOrStandard retrieveMethodOrStandard2 = methodOrStandardRepository.getByName(retrieveMethodOrStandard.getName());
        Assert.assertNotNull(retrieveMethodOrStandard2);
        Assert.assertEquals(retrieveMethodOrStandard2.getName(), retrieveMethodOrStandard.getName());

        methodOrStandardRepository.delete(retrieveMethodOrStandard2.getId());

        MethodOrStandard retrieveMethodOrStandard3 = methodOrStandardRepository.getByName(retrieveMethodOrStandard.getName());
        Assert.assertNull(retrieveMethodOrStandard3);
    }

    @Test
    public void testParameter() {
        Parameter retrieveParameter0 = parameterRepository.getByName(NAME);
        if (retrieveParameter0 != null) {
            parameterRepository.delete(retrieveParameter0.getId());
        }
        // Add and retrieve
        Parameter parameter = new Parameter();
        parameter.setName(NAME);
        parameterRepository.save(parameter);

        Parameter retrieveParameter = parameterRepository.getByName(NAME);
        Assert.assertNotNull(retrieveParameter.getName());

        Parameter retrieveParameter2 = parameterRepository.getByName(retrieveParameter.getName());
        Assert.assertNotNull(retrieveParameter2);
        Assert.assertEquals(retrieveParameter2.getName(), retrieveParameter.getName());

        // Test the case-insensitive cache
        Assert.assertEquals(NAME, lookupService.relaxed().find(Parameter.class, NAME_MASH).getName());

        // Second test to prove cache use
        Assert.assertEquals(NAME, lookupService.relaxed().find(Parameter.class, NAME_MASH).getName());

        parameterRepository.delete(retrieveParameter2.getId());
        parameterRepository.flush();

        Assert.assertNull(lookupService.relaxed().find(Parameter.class, NAME_MASH));

        Parameter retrieveParameter3 = parameterRepository.getByName(retrieveParameter.getName());
        Assert.assertNull(retrieveParameter3);
    }

    @Test
    public void testReturnType() {
        final String LANDFILL = "Landfill";
        // Make the test type is gone
        ReturnType retrieveReturnType0 = returnTypeRepository.getByName(NAME);

        removeTestData(returnTypeRepository, retrieveReturnType0);
        // Add and retrieve
        ReturnType returnType = new ReturnType();
        returnType.setName(NAME);
        returnType.setSector(LANDFILL);
        returnTypeRepository.save(returnType);

        ReturnType retrieveReturnType = returnTypeRepository.getByName(NAME);
        Assert.assertNotNull(retrieveReturnType.getName());

        ReturnType retrieveReturnType2 = returnTypeRepository.getByName(retrieveReturnType.getName());
        Assert.assertNotNull(retrieveReturnType2);
        Assert.assertEquals(retrieveReturnType2.getName(), retrieveReturnType.getName());

        // Test the case-insensitive cache
        Assert.assertEquals(NAME, lookupService.relaxed().find(ReturnType.class, NAME_MASH).getName());

        // Second test to prove cache use
        Assert.assertEquals(NAME, lookupService.relaxed().find(ReturnType.class, NAME_MASH).getName());

        // Remove it and try again
        returnTypeRepository.delete(retrieveReturnType2.getId());
        Assert.assertNull(lookupService.relaxed().find(ReturnType.class, NAME_MASH));

        ReturnType retrieveReturnType3 = returnTypeRepository.getByName(retrieveReturnType.getName());
        Assert.assertNull(retrieveReturnType3);
    }

    @Test
    public void testUnit() {
        // Make the test type is gone
        Unit retrieveUnit0 = unitRepository.getByName(NAME);
        if (retrieveUnit0 != null) {
            unitRepository.delete(retrieveUnit0.getId());
        }
        // Add and retrieve
        Unit unit = new Unit();
        unit.setName(NAME);
        unit.setDescription(NAME + NAME + NAME);
        unit.setType("Type");
        unit.setLongName(NAME + NAME);
        unit.setUnicode("UC");

        unitRepository.save(unit);

        Unit retrieveUnit = unitRepository.getByName(NAME);
        Assert.assertNotNull(retrieveUnit.getName());
        Assert.assertEquals(retrieveUnit.getDescription(), NAME + NAME + NAME);
        Assert.assertEquals(retrieveUnit.getLongName(), NAME + NAME);
        Assert.assertEquals(retrieveUnit.getType(), "Type");
        Assert.assertEquals(retrieveUnit.getUnicode(), "UC");

        Unit retrieveUnit2 = unitRepository.getByName(retrieveUnit.getName());
        Assert.assertNotNull(retrieveUnit2);
        Assert.assertEquals(retrieveUnit2.getName(), retrieveUnit.getName());

        unitRepository.delete(retrieveUnit2.getId());

        Unit retrieveUnit3 = unitRepository.getByName(retrieveUnit.getName());
        Assert.assertNull(retrieveUnit3);
    }

    @Test
    public void testQualifier() {
        // Make the test type is gone
        Qualifier retrieveQualifier0 = qualifierRepository.getByName(NAME);
        if (retrieveQualifier0 != null) {
            qualifierRepository.delete(retrieveQualifier0.getId());
        }
        // Add and retrieve
        Qualifier Qualifier = new Qualifier();
        Qualifier.setName(NAME);
        Qualifier.setNotes(NAME);
        qualifierRepository.save(Qualifier);

        Qualifier retrieveQualifier = qualifierRepository.getByName(NAME);
        Assert.assertNotNull(retrieveQualifier.getName());
        Qualifier retrieveQualifier2 = qualifierRepository.getByName(retrieveQualifier.getName());
        Assert.assertNotNull(retrieveQualifier2);
        Assert.assertEquals(retrieveQualifier2.getName(), retrieveQualifier.getName());
        qualifierRepository.delete(retrieveQualifier2.getId());

        Qualifier retrieveQualifier3 = qualifierRepository.getByName(retrieveQualifier.getName());
        Assert.assertNull(retrieveQualifier3);
    }

    @Test
    public void testTextValues() {
        TextValue textValue0 = textValueRepository.getByName(NAME);
        if (textValue0 != null) {
            qualifierRepository.delete(textValue0.getId());
        }
        // Add and retrieve
        TextValue TextValue = new TextValue();
        TextValue.setName(NAME);
        textValueRepository.save(TextValue);

        TextValue retrieveTextValue = textValueRepository.getByName(NAME);
        Assert.assertNotNull(retrieveTextValue.getName());

        TextValue retrieveTextValue2 = textValueRepository.getByName(retrieveTextValue.getName());
        Assert.assertNotNull(retrieveTextValue2);
        Assert.assertEquals(retrieveTextValue2.getName(), retrieveTextValue.getName());

        textValueRepository.delete(retrieveTextValue2.getId());

        TextValue retrieveTextValue3 = textValueRepository.getByName(retrieveTextValue.getName());
        Assert.assertNull(retrieveTextValue3);
    }

    @Test
    public void testCacheDissimilarObjectsBySameKey() {
        final String name = "DUPLICATE NAME";
        final String mash = "dupLICATE     nAmE";

        Parameter p = new Parameter();
        p.setName(name);
        parameterRepository.save(p);

        ReferencePeriod r = new ReferencePeriod();
        r.setName(name);
        referencePeriodRepository.save(r);

        try {
            Parameter p1 = lookupService.strict().find(Parameter.class, name);
            ReferencePeriod r1 = lookupService.strict().find(ReferencePeriod.class, name);
            Assert.assertEquals(p, p1);
            Assert.assertEquals(r, r1);

            Parameter p2 = lookupService.relaxed().find(Parameter.class, mash);
            ReferencePeriod r2 = lookupService.relaxed().find(ReferencePeriod.class, mash);
            Assert.assertEquals(p, p2);
            Assert.assertEquals(r, r2);
        } finally {
            removeTestData(parameterRepository, p);
            removeTestData(referencePeriodRepository, r);
        }
    }

    @Test
    public void testAlias() {
        final String PRIMARY_1 = "__TEST__ Primary 1";
        final String ALIAS_1 = "__TEST__ Alias 1";
        final String ALIAS_2 = "__TEST__ Alias 2";
        final String ALIAS_3 = "__TEST__ Alias 3";

        final String PRIMARY_1_MASH = "__TEST__    PrIMARy     1";
        final String ALIAS_1_MASH = "__TEST__ aLiAS    1";
        final String ALIAS_2_MASH = "__TEST__ aLIas 2";
        final String ALIAS_3_MASH = "__TEST__ aLIas          3";

        // Ensure any existing test data is removed (primary value deletion always cascades deletion to aliases)
        removeTestData(referencePeriodRepository, referencePeriodRepository.getByName(PRIMARY_1));

        ReferencePeriod primaryEntity = new ReferencePeriod();
        primaryEntity.setName(PRIMARY_1);

        ReferencePeriod alias1 = new ReferencePeriod();
        alias1.setName(ALIAS_1);
        alias1.setPreferred(primaryEntity);

        ReferencePeriod alias2 = new ReferencePeriod();
        alias2.setName(ALIAS_2);
        alias2.setPreferred(primaryEntity);

        ReferencePeriod alias3 = new ReferencePeriod();
        alias3.setName(ALIAS_3);
        alias3.setPreferred(primaryEntity);

        primaryEntity.setAliases(new HashSet<>(Arrays.asList(alias1, alias2)));

        // Save the primary entity along with the two existing aliases
        primaryEntity = referencePeriodRepository.saveAndFlush(primaryEntity);
        Assert.assertTrue(primaryEntity.getAliases().size() == 2);

        // Now save a third alias
        referencePeriodRepository.saveAndFlush(alias3);

        // Alias list still be 2 as we haven't refreshed the primary entity
        Assert.assertTrue(primaryEntity.getAliases().size() == 2);

        // All other lookups will return the 3rd alias
        List<ReferencePeriod> list = IterableUtils.toList(referencePeriodRepository.findAll());
        Assert.assertTrue(list.contains(primaryEntity));
        Assert.assertTrue(list.contains(alias1));
        Assert.assertTrue(list.contains(alias2));
        Assert.assertTrue(list.contains(alias3));

        Assert.assertTrue(lookupService.relaxed().find(ReferencePeriod.class, PRIMARY_1_MASH).isPrimary());

        Assert.assertEquals(PRIMARY_1, lookupService.strict().find(ReferencePeriod.class, PRIMARY_1).getPrimary().getName());
        Assert.assertEquals(PRIMARY_1, lookupService.strict().find(ReferencePeriod.class, ALIAS_1).getPrimary().getName());
        Assert.assertEquals(PRIMARY_1, lookupService.strict().find(ReferencePeriod.class, ALIAS_2).getPrimary().getName());
        Assert.assertEquals(PRIMARY_1, lookupService.strict().find(ReferencePeriod.class, ALIAS_3).getPrimary().getName());

        Assert.assertEquals(PRIMARY_1, lookupService.relaxed().find(ReferencePeriod.class, PRIMARY_1_MASH).getPrimary().getName());
        Assert.assertEquals(PRIMARY_1, lookupService.relaxed().find(ReferencePeriod.class, ALIAS_1_MASH).getPrimary().getName());
        Assert.assertEquals(PRIMARY_1, lookupService.relaxed().find(ReferencePeriod.class, ALIAS_2_MASH).getPrimary().getName());
        Assert.assertEquals(PRIMARY_1, lookupService.relaxed().find(ReferencePeriod.class, ALIAS_3_MASH).getPrimary().getName());

        // We can refresh the primary entity and we'll see the 3rd alias
        primaryEntity = lookupService.strict().find(ReferencePeriod.class, PRIMARY_1);
        Assert.assertTrue(primaryEntity.getAliases().size() == 3);

        // Finally, cascade delete all of the test data
        removeTestData(referencePeriodRepository, primaryEntity);
    }

    @SafeVarargs
    private final <E extends MasterDataEntity> void removeTestData(MasterDataRepository<E> repository, E... entities) {
        for (E entity : entities) {
            if (entity != null) {
                LOGGER.info("Attempting to delete test data entry " + entity.getClass().getSimpleName()
                        + " with id=" + entity.getId() + " and name=" + entity.getName());
                repository.delete(entity);
            }
        }
        repository.flush();
    }

}

