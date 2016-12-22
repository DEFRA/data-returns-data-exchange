package uk.gov.ea.datareturns.tests.domain.model;

import org.apache.commons.lang3.tuple.Pair;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.junit4.SpringRunner;
import uk.gov.ea.datareturns.App;
import uk.gov.ea.datareturns.domain.jpa.dao.ParameterDao;
import uk.gov.ea.datareturns.domain.jpa.dao.ReleasesAndTransfersDao;
import uk.gov.ea.datareturns.domain.jpa.dao.ReturnTypeDao;
import uk.gov.ea.datareturns.domain.jpa.dao.UnitDao;
import uk.gov.ea.datareturns.domain.jpa.entities.*;
import uk.gov.ea.datareturns.domain.jpa.hierarchy.Hierarchy;
import uk.gov.ea.datareturns.domain.jpa.hierarchy.HierarchyLevel;
import uk.gov.ea.datareturns.domain.jpa.hierarchy.implementations.ParameterHierarchy;

import javax.inject.Inject;

/**
 * Created by graham on 17/11/16.
 */
@SpringBootTest(classes=App.class)
@DirtiesContext
@RunWith(SpringRunner.class)
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
    ParameterHierarchy parameterHierarchy;


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
     * With disallowed parameter
     */
    @Test
    public void dependencyLandfillWrongParameter() {
        ReturnType returnType = returnTypeDao.getByName("Ambient air quality");
        Assert.assertNotNull(returnType);

        Parameter parameter = parameterDao.getByName("Krypton-85");
        Assert.assertNotNull(parameter);

        Unit unit = unitDao.getByName("cm3/hr");
        Assert.assertNotNull(unit);

        Pair<HierarchyLevel<? extends Hierarchy.HierarchyEntity>, Hierarchy.Result> result
                = parameterHierarchy.validate(returnType, parameter, unit);

        Assert.assertEquals(ControlledListsList.PARAMETER, result.getLeft().getControlledList());
        Assert.assertEquals(Hierarchy.Result.EXCLUDED, result.getRight());
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

        Unit unit = unitDao.getByName("µScm⁻¹");
        Assert.assertNotNull(unit);

        ReleasesAndTransfers releasesAndTransfers = releasesAndTransfersDao.getByName("Controlled Water");
        Assert.assertNotNull(releasesAndTransfers);

        Pair<HierarchyLevel<? extends Hierarchy.HierarchyEntity>, Hierarchy.Result> result
                = parameterHierarchy.validate(returnType, releasesAndTransfers, parameter, unit);

        Assert.assertEquals(ControlledListsList.RELEASES_AND_TRANSFER, result.getLeft().getControlledList());
        Assert.assertEquals(Hierarchy.Result.NOT_EXPECTED, result.getRight());

    }

    /*
     * Pollution Inventory happy path
     */
    @Test
    public void dependencyPollutionInventoryHappyPath1() {
        ReturnType returnType = returnTypeDao.getByName("Pollution Inventory");
        Assert.assertNotNull(returnType);

        Parameter parameter = parameterDao.getByName("Aldrin");
        Assert.assertNotNull(parameter);

        ReleasesAndTransfers releasesAndTransfers = releasesAndTransfersDao.getByName("Air");
        Assert.assertNotNull(releasesAndTransfers);

        Unit unit = unitDao.getByName("g");
        Assert.assertNotNull(unit);

        Pair<HierarchyLevel<? extends Hierarchy.HierarchyEntity>, Hierarchy.Result> result
                = parameterHierarchy.validate(returnType, releasesAndTransfers, parameter, unit);

        Assert.assertEquals(Hierarchy.Result.OK, result.getRight());
    }

    /*
     * No unit - this is an error for pollution inventory
     */
    @Test
    public void dependencyPollutionInventoryNoUnit() {
        ReturnType returnType = returnTypeDao.getByName("Pollution Inventory");
        Assert.assertNotNull(returnType);

        Parameter parameter = parameterDao.getByName("Aldrin");
        Assert.assertNotNull(parameter);

        ReleasesAndTransfers releasesAndTransfers = releasesAndTransfersDao.getByName("Air");
        Assert.assertNotNull(releasesAndTransfers);

        Pair<HierarchyLevel<? extends Hierarchy.HierarchyEntity>, Hierarchy.Result> result
                = parameterHierarchy.validate(returnType, releasesAndTransfers, parameter, null);

        Assert.assertEquals(ControlledListsList.UNIT, result.getLeft().getControlledList());
        Assert.assertEquals(Hierarchy.Result.EXPECTED, result.getRight());
    }

    /*
     * Wrong unit
     */
    @Test
    public void dependencyPollutionInventoryWrongUnits() {
        ReturnType returnType = returnTypeDao.getByName("Pollution Inventory");
        Assert.assertNotNull(returnType);

        Parameter parameter = parameterDao.getByName("Alachlor");
        Assert.assertNotNull(parameter);

        ReleasesAndTransfers releasesAndTransfers = releasesAndTransfersDao.getByName("Controlled Water");
        Assert.assertNotNull(releasesAndTransfers);

        Unit unit = unitDao.getByName("µgkg⁻¹");
        Assert.assertNotNull(unit);

        Pair<HierarchyLevel<? extends Hierarchy.HierarchyEntity>, Hierarchy.Result> result
                = parameterHierarchy.validate(returnType, releasesAndTransfers, parameter, unit);

        Assert.assertEquals(ControlledListsList.UNIT, result.getLeft().getControlledList());
        Assert.assertEquals(Hierarchy.Result.NOT_IN_GROUP, result.getRight());
    }

    @Test
    public void dependencyPollutionInventoryWrongParameter() {
        ReturnType returnType = returnTypeDao.getByName("Pollution Inventory");
        Assert.assertNotNull(returnType);

        Parameter parameter = parameterDao.getByName("Diethylenetriamine");
        Assert.assertNotNull(parameter);

        ReleasesAndTransfers releasesAndTransfers = releasesAndTransfersDao.getByName("Controlled Water");
        Assert.assertNotNull(releasesAndTransfers);

        Unit unit = unitDao.getByName("kg");
        Assert.assertNotNull(unit);

        Pair<HierarchyLevel<? extends Hierarchy.HierarchyEntity>, Hierarchy.Result> result
                = parameterHierarchy.validate(returnType, releasesAndTransfers, parameter, unit);

        Assert.assertEquals(ControlledListsList.PARAMETER, result.getLeft().getControlledList());
        Assert.assertEquals(Hierarchy.Result.NOT_FOUND, result.getRight());
    }

    @Test
    public void dependencyPollutionInventoryNoParameter() {
        ReturnType returnType = returnTypeDao.getByName("Pollution Inventory");
        Assert.assertNotNull(returnType);

        ReleasesAndTransfers releasesAndTransfers = releasesAndTransfersDao.getByName("Controlled Water");
        Assert.assertNotNull(releasesAndTransfers);

        Unit unit = unitDao.getByName("kg");
        Assert.assertNotNull(unit);

        Pair<HierarchyLevel<? extends Hierarchy.HierarchyEntity>, Hierarchy.Result> result
                = parameterHierarchy.validate(returnType, releasesAndTransfers, null, unit);

        Assert.assertEquals(ControlledListsList.PARAMETER, result.getLeft().getControlledList());
        Assert.assertEquals(Hierarchy.Result.EXPECTED, result.getRight());
    }

    @Test
    public void dependencyPollutionInventoryNoRelease() {
        ReturnType returnType = returnTypeDao.getByName("Pollution Inventory");
        Assert.assertNotNull(returnType);

        Parameter parameter = parameterDao.getByName("Diethylenetriamine");
        Assert.assertNotNull(parameter);

        Unit unit = unitDao.getByName("kg");
        Assert.assertNotNull(unit);

        Pair<HierarchyLevel<? extends Hierarchy.HierarchyEntity>, Hierarchy.Result> result
                = parameterHierarchy.validate(returnType, null, parameter, unit);

        Assert.assertEquals(ControlledListsList.RELEASES_AND_TRANSFER, result.getLeft().getControlledList());
        Assert.assertEquals(Hierarchy.Result.EXPECTED, result.getRight());
    }


    @Test
    public void nothing() {
        Pair<HierarchyLevel<? extends Hierarchy.HierarchyEntity>, Hierarchy.Result> result
                = parameterHierarchy.validate(null, null, null, null);

        // We don't need to check the level (terminating entity for the happy path
        Assert.assertEquals(ControlledListsList.RETURN_TYPE, result.getLeft().getControlledList());
        Assert.assertEquals(Hierarchy.Result.EXPECTED, result.getRight());
    }

    /*
     * Group validator tests
     * Pollution Inventory,Air,Xenon 133,[Radioactivity]
     * Pollution Inventory,Air,Americium 241,[Mass]
     * Pollution Inventory,Air,Americium 241,^mol
     */
    @Test
    public void dependencyTestGroupRadioactivityHappy() {
        ReturnType returnType = returnTypeDao.getByName("Pollution Inventory");
        Assert.assertNotNull(returnType);

        ReleasesAndTransfers releasesAndTransfers = releasesAndTransfersDao.getByName("Air");
        Assert.assertNotNull(releasesAndTransfers);

        Parameter parameter = parameterDao.getByName("Xenon-133");
        Assert.assertNotNull(parameter);

        Unit unit = unitDao.getByName("Bq/m2");
        Assert.assertNotNull(unit);

        Pair<HierarchyLevel<? extends Hierarchy.HierarchyEntity>, Hierarchy.Result> result
                = parameterHierarchy.validate(returnType, releasesAndTransfers, parameter, unit);

        Assert.assertEquals(Hierarchy.Result.OK, result.getRight());
    }

    @Test
    public void dependencyTestGroupMassHappy() {
        ReturnType returnType = returnTypeDao.getByName("Pollution Inventory");
        Assert.assertNotNull(returnType);

        ReleasesAndTransfers releasesAndTransfers = releasesAndTransfersDao.getByName("Air");
        Assert.assertNotNull(releasesAndTransfers);

        Parameter parameter = parameterDao.getByName("Americium-241");
        Assert.assertNotNull(parameter);

        Unit unit = unitDao.getByName("g");
        Assert.assertNotNull(unit);

        Pair<HierarchyLevel<? extends Hierarchy.HierarchyEntity>, Hierarchy.Result> result
                = parameterHierarchy.validate(returnType, releasesAndTransfers, parameter, unit);

        Assert.assertEquals(Hierarchy.Result.OK, result.getRight());
    }

    @Test
    public void dependencyTestGroupMassInvalid1() {
        ReturnType returnType = returnTypeDao.getByName("Pollution Inventory");
        Assert.assertNotNull(returnType);

        ReleasesAndTransfers releasesAndTransfers = releasesAndTransfersDao.getByName("Air");
        Assert.assertNotNull(releasesAndTransfers);

        Parameter parameter = parameterDao.getByName("Americium-241");
        Assert.assertNotNull(parameter);

        Unit unit = unitDao.getByName("µg/hr");
        Assert.assertNotNull(unit);

        Pair<HierarchyLevel<? extends Hierarchy.HierarchyEntity>, Hierarchy.Result> result
                = parameterHierarchy.validate(returnType, releasesAndTransfers, parameter, unit);

        Assert.assertEquals(Hierarchy.Result.NOT_IN_GROUP, result.getRight());
        Assert.assertEquals(ControlledListsList.UNIT, result.getLeft().getControlledList());
    }
}
