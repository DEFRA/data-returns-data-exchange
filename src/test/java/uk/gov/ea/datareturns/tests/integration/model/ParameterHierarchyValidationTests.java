package uk.gov.ea.datareturns.tests.integration.model;

import org.apache.commons.lang3.tuple.Pair;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;
import uk.gov.ea.datareturns.App;
import uk.gov.ea.datareturns.domain.jpa.dao.masterdata.*;
import uk.gov.ea.datareturns.domain.jpa.entities.masterdata.impl.*;
import uk.gov.ea.datareturns.domain.jpa.hierarchy.Hierarchy;
import uk.gov.ea.datareturns.domain.jpa.hierarchy.HierarchyLevel;
import uk.gov.ea.datareturns.domain.jpa.hierarchy.implementations.ParameterHierarchy;

import javax.inject.Inject;

/**
 * Created by graham on 17/11/16.
 */
@RunWith(SpringRunner.class)
@SpringBootTest(classes = App.class, webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
@ActiveProfiles("IntegrationTests")
public class ParameterHierarchyValidationTests {
    @Inject
    private ParameterDao parameterDao;

    @Inject
    private ReturnTypeDao returnTypeDao;

    @Inject
    private ReleasesAndTransfersDao releasesAndTransfersDao;

    @Inject
    private UnitDao unitDao;

    @Inject
    private ParameterHierarchy parameterHierarchy;

    /*
     * Test landfill happy paths - with unit
     */
    @Test
    public void dependencyLandfillHappy2() {
        ReturnType returnType = returnTypeDao.getByName("Emissions to groundwater");
        Assert.assertNotNull(returnType);

        Parameter parameter = parameterDao.getByName("Benzoic acid, p-tert-butyl");
        Assert.assertNotNull(parameter);

        Unit unit = unitDao.getByName("cm3/hr");
        Assert.assertNotNull(unit);

        Pair<HierarchyLevel<? extends Hierarchy.HierarchyEntity>, Hierarchy.Result> result
                = parameterHierarchy.validate(returnType, parameter, unit);

        Assert.assertEquals(Hierarchy.Result.OK, result.getRight());
    }

    /*
     * With no parameter
     */
    @Test
    public void dependencyLandfillNoParameter() {
        ReturnType returnType = returnTypeDao.getByName("Ambient air quality");
        Assert.assertNotNull(returnType);

        Unit unit = unitDao.getByName("cm3/hr");
        Assert.assertNotNull(unit);

        Pair<HierarchyLevel<? extends Hierarchy.HierarchyEntity>, Hierarchy.Result> result
                = parameterHierarchy.validate(returnType, null, unit);

        Assert.assertEquals(ControlledListsList.PARAMETER, result.getLeft().getControlledList());
        Assert.assertEquals(Hierarchy.Result.EXPECTED, result.getRight());

    }

    /*
     * The should be excluded by using  "^*" - none should be given
     */
    @Test
    public void dependencyLandfillSpecifyReleasesInError() {
        ReturnType returnType = returnTypeDao.getByName("Emissions to sewer");
        Assert.assertNotNull(returnType);

        Parameter parameter = parameterDao.getByName("Diethylenetriamine");
        Assert.assertNotNull(parameter);

        Unit unit = unitDao.getByNameOrAlias(Key.relaxed("µScm⁻¹"));
        Assert.assertNotNull(unit);

        ReleasesAndTransfers releasesAndTransfers = releasesAndTransfersDao.getByName("Controlled Water");
        Assert.assertNotNull(releasesAndTransfers);

        Pair<HierarchyLevel<? extends Hierarchy.HierarchyEntity>, Hierarchy.Result> result
                = parameterHierarchy.validate(returnType, releasesAndTransfers, parameter, unit);

        Assert.assertEquals(ControlledListsList.RELEASES_AND_TRANSFER, result.getLeft().getControlledList());
        Assert.assertEquals(Hierarchy.Result.NOT_EXPECTED, result.getRight());

    }

    @Test
    public void nothing() {
        Pair<HierarchyLevel<? extends Hierarchy.HierarchyEntity>, Hierarchy.Result> result
                = parameterHierarchy.validate(null, null, null, null);

        // We don't need to check the level (terminating entity for the happy path
        Assert.assertEquals(ControlledListsList.RETURN_TYPE, result.getLeft().getControlledList());
        Assert.assertEquals(Hierarchy.Result.EXPECTED, result.getRight());
    }

}
