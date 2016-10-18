package uk.gov.ea.datareturns.tests.domain.model;

import org.apache.commons.lang3.tuple.Pair;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.junit4.SpringRunner;
import uk.gov.ea.datareturns.App;
import uk.gov.ea.datareturns.domain.jpa.dao.*;
import uk.gov.ea.datareturns.domain.jpa.entities.*;
import uk.gov.ea.datareturns.domain.jpa.service.DependencyNavigation;
import uk.gov.ea.datareturns.domain.jpa.service.DependencyValidation;
import uk.gov.ea.datareturns.domain.jpa.service.DependencyValidation.Result;

import javax.inject.Inject;
import java.util.List;

/**
 * Created by graham on 03/10/16.
 */
@SpringBootTest(classes=App.class)
@DirtiesContext
@RunWith(SpringRunner.class)
public class DependenciesTests {

    protected static final Logger LOGGER = LoggerFactory.getLogger(DependenciesTests.class);

    @Inject
    DependenciesDao dao;

    @Inject
    private ParameterDao parameterDao;

    @Inject
    private ReturnTypeDao returnTypeDao;

    @Inject
    private ReleasesAndTransfersDao releasesAndTransfersDao;

    @Inject
    private UnitDao unitDao;

    @Inject
    private DependencyValidation dependencyValidation;

    @Inject
    private DependencyNavigation dependencyNavigation;

    @Test
    public void listDependencies() {
        List<Dependencies> dependencies = dao.list();
        Assert.assertNotNull(dependencies);
        Assert.assertNotEquals(dependencies.size(), 0);
        //for (Dependencies d : dependencies) {
        //    System.out.println(d.toString());
        //}
    }

    @Test
    public void buildCache() {
        Assert.assertNotNull(dao.buildCache());
    }

    /*
     * Test landfill happy paths - no unit
     */
    @Test
    public void dependencyLandfillHappy1() {
        ReturnType returnType = returnTypeDao.getByName("Emissions to groundwater");
        Assert.assertNotNull(returnType);

        Parameter parameter = parameterDao.getByName("Trichlorobenzene");
        Assert.assertNotNull(parameter);

        Pair<ControlledListsList, Result> result
                = dependencyValidation.validate(returnType, parameter);

        // We don't need to check the level (terminating entity for the happy path
        Assert.assertEquals(Pair.of(null,
                Result.OK).getRight(), result.getRight());
    }

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

        Pair<ControlledListsList, Result> result
                = dependencyValidation.validate(returnType, parameter, unit);
        Assert.assertEquals(Pair.of(null,
                Result.OK).getRight(), result.getRight());
    }

    /*
     * With disallowed parameter
     */
    @Test
    public void dependencyLandfillWrongParameter() {
        ReturnType returnType = returnTypeDao.getByName("Ambient air quality");
        Assert.assertNotNull(returnType);

        Parameter parameter = parameterDao.getByName("Krypton 85");
        Assert.assertNotNull(parameter);

        Unit unit = unitDao.getByName("cm3/hr");
        Assert.assertNotNull(unit);

        Pair<ControlledListsList, Result> result
                = dependencyValidation.validate(returnType, parameter, unit);

        Assert.assertEquals(Pair.of(ControlledListsList.PARAMETERS,
                Result.EXCLUDED), result);
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

        Pair<ControlledListsList, Result> result
                = dependencyValidation.validate(returnType, null, unit);

        Assert.assertEquals(Pair.of(ControlledListsList.PARAMETERS,
                Result.EXPECTED), result);
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

        Pair<ControlledListsList, Result> result
                = dependencyValidation.validate(returnType, releasesAndTransfers, parameter, unit);

        Assert.assertEquals(Pair.of(ControlledListsList.RELEASES_AND_TRANSFERS,
                Result.NOT_EXPECTED), result);
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

        Pair<ControlledListsList, Result> result
                = dependencyValidation.validate(returnType, releasesAndTransfers, parameter, unit);

        Assert.assertEquals(Pair.of(null,
                Result.OK).getRight(), result.getRight());
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

        Pair<ControlledListsList, Result> result
                = dependencyValidation.validate(returnType, releasesAndTransfers, parameter, null);

        Assert.assertEquals(Pair.of(ControlledListsList.UNITS,
                Result.EXPECTED), result);
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

        Pair<ControlledListsList, Result> result
                        = dependencyValidation.validate(returnType, releasesAndTransfers, parameter, unit);

        Assert.assertEquals(Pair.of(ControlledListsList.UNITS,
                Result.NOT_FOUND), result);
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

        Pair<ControlledListsList, Result> result
                = dependencyValidation.validate(returnType, releasesAndTransfers, parameter, unit);

        Assert.assertEquals(Pair.of(ControlledListsList.PARAMETERS,
                Result.NOT_FOUND), result);
    }

    @Test
    public void dependencyPollutionInventoryNoParameter() {
        ReturnType returnType = returnTypeDao.getByName("Pollution Inventory");
        Assert.assertNotNull(returnType);

        ReleasesAndTransfers releasesAndTransfers = releasesAndTransfersDao.getByName("Controlled Water");
        Assert.assertNotNull(releasesAndTransfers);

        Unit unit = unitDao.getByName("kg");
        Assert.assertNotNull(unit);

        Pair<ControlledListsList, Result> result
                = dependencyValidation.validate(returnType, releasesAndTransfers, null, unit);

        Assert.assertEquals(Pair.of(ControlledListsList.PARAMETERS,
                Result.EXPECTED), result);
    }

    @Test
    public void dependencyPollutionInventoryNoRelease() {
        ReturnType returnType = returnTypeDao.getByName("Pollution Inventory");
        Assert.assertNotNull(returnType);

        Parameter parameter = parameterDao.getByName("Diethylenetriamine");
        Assert.assertNotNull(parameter);

        Unit unit = unitDao.getByName("kg");
        Assert.assertNotNull(unit);

        Pair<ControlledListsList, Result> result
                = dependencyValidation.validate(returnType, null, parameter, unit);

        Assert.assertEquals(Pair.of(ControlledListsList.RELEASES_AND_TRANSFERS,
                Result.EXPECTED), result);
    }

    @Test
    public void dependencyREMHappyPath1() {
        ReturnType returnType = returnTypeDao.getByName("REM Return");
        Assert.assertNotNull(returnType);

        Parameter parameter = parameterDao.getByName("Net Water Used");
        Assert.assertNotNull(parameter);

        Unit unit = unitDao.getByName("m3");
        Assert.assertNotNull(unit);

        Pair<ControlledListsList, Result> result
                = dependencyValidation.validate(returnType, parameter, unit);

        Assert.assertEquals(Pair.of(null,
                Result.OK).getRight(), result.getRight());
    }

    @Test
    public void dependencyREMWrongUnit() {
        ReturnType returnType = returnTypeDao.getByName("REM Return");
        Assert.assertNotNull(returnType);

        Parameter parameter = parameterDao.getByName("Net Water Used");
        Assert.assertNotNull(parameter);

        Unit unit = unitDao.getByName("t");
        Assert.assertNotNull(unit);

        Pair<ControlledListsList, Result> result
                = dependencyValidation.validate(returnType, parameter, unit);

        Assert.assertEquals(Pair.of(ControlledListsList.UNITS,
                Result.NOT_FOUND), result);
    }

    @Test
    public void dependencyREMNoUnit() {
        ReturnType returnType = returnTypeDao.getByName("REM Return");
        Assert.assertNotNull(returnType);

        Parameter parameter = parameterDao.getByName("Net Water Used");
        Assert.assertNotNull(parameter);

        Pair<ControlledListsList, Result> result
                = dependencyValidation.validate(returnType, parameter, null);

        Assert.assertEquals(Pair.of(ControlledListsList.UNITS,
                Result.EXPECTED), result);
    }

    @Test
    public void dependencyREMWrongParameter() {
        ReturnType returnType = returnTypeDao.getByName("REM Return");
        Assert.assertNotNull(returnType);

        Parameter parameter = parameterDao.getByName("Diethylenetriamine");
        Assert.assertNotNull(parameter);

        Pair<ControlledListsList, Result> result
                = dependencyValidation.validate(returnType, parameter, null);

        Assert.assertEquals(Pair.of(ControlledListsList.PARAMETERS,
                Result.NOT_FOUND), result);
    }

    @Test
    public void dependencyREMNoParameter() {
        ReturnType returnType = returnTypeDao.getByName("REM Return");
        Assert.assertNotNull(returnType);

        Pair<ControlledListsList, Result> result
                = dependencyValidation.validate(returnType, null, null);

        Assert.assertEquals(Pair.of(ControlledListsList.PARAMETERS,
                Result.EXPECTED), result);
    }

    @Test
    public void dependencyREMUnexpectedRelease() {
        ReturnType returnType = returnTypeDao.getByName("REM Return");
        Assert.assertNotNull(returnType);

        ReleasesAndTransfers releasesAndTransfers = releasesAndTransfersDao.getByName("Controlled Water");
        Assert.assertNotNull(releasesAndTransfers);

        Parameter parameter = parameterDao.getByName("Net Water Used");
        Assert.assertNotNull(parameter);

        Unit unit = unitDao.getByName("t");
        Assert.assertNotNull(unit);

        Pair<ControlledListsList, Result> result
                = dependencyValidation.validate(returnType, releasesAndTransfers, parameter, unit);

        Assert.assertEquals(Pair.of(ControlledListsList.RELEASES_AND_TRANSFERS,
                Result.NOT_EXPECTED), result);
    }

    @Test
    public void dependencyLCPHappy() {
        ReturnType returnType = returnTypeDao.getByName("IED Chap 3 Inventory");
        Assert.assertNotNull(returnType);

        Parameter parameter = parameterDao.getByName("Net Energy Input Other solid fuels");
        Assert.assertNotNull(parameter);

        Unit unit = unitDao.getByName("TJ");
        Assert.assertNotNull(unit);

        Pair<ControlledListsList, Result> result
                = dependencyValidation.validate(returnType, parameter, unit);

        Assert.assertEquals(Pair.of(null,
                Result.OK).getRight(), result.getRight());
    }

    @Test
    public void dependencyLCPWrongUnit() {
        ReturnType returnType = returnTypeDao.getByName("IED Chap 3 Inventory");
        Assert.assertNotNull(returnType);

        Parameter parameter = parameterDao.getByName("Net Energy Input Other solid fuels");
        Assert.assertNotNull(parameter);

        Unit unit = unitDao.getByName("g");
        Assert.assertNotNull(unit);

        Pair<ControlledListsList, Result> result
                = dependencyValidation.validate(returnType, parameter, unit);

        Assert.assertEquals(Pair.of(ControlledListsList.UNITS,
                Result.NOT_FOUND), result);
    }

    @Test
    public void dependencyLCPNoUnit() {
        ReturnType returnType = returnTypeDao.getByName("IED Chap 3 Inventory");
        Assert.assertNotNull(returnType);

        Parameter parameter = parameterDao.getByName("Net Energy Input Other solid fuels");
        Assert.assertNotNull(parameter);

        Pair<ControlledListsList, Result> result
                = dependencyValidation.validate(returnType, parameter, null);

        Assert.assertEquals(Pair.of(ControlledListsList.UNITS,
                Result.EXPECTED), result);
    }

    @Test
    public void dependencyLCPWrongParameter() {
        ReturnType returnType = returnTypeDao.getByName("IED Chap 3 Inventory");
        Assert.assertNotNull(returnType);

        Parameter parameter = parameterDao.getByName("Diethylenetriamine");
        Assert.assertNotNull(parameter);

        Unit unit = unitDao.getByName("TJ");
        Assert.assertNotNull(unit);

        Pair<ControlledListsList, Result> result
                = dependencyValidation.validate(returnType, parameter, unit);

        Assert.assertEquals(Pair.of(ControlledListsList.PARAMETERS,
                Result.NOT_FOUND), result);
    }

    @Test
    public void dependencyLCPNoParameter() {
        ReturnType returnType = returnTypeDao.getByName("IED Chap 3 Inventory");
        Assert.assertNotNull(returnType);

        Unit unit = unitDao.getByName("TJ");
        Assert.assertNotNull(unit);

        Pair<ControlledListsList, Result> result
                = dependencyValidation.validate(returnType, null, unit);

        Assert.assertEquals(Pair.of(ControlledListsList.PARAMETERS,
                Result.EXPECTED), result);
    }

    @Test
    public void dependencyLCPUnexpectedRelease() {
        ReturnType returnType = returnTypeDao.getByName("IED Chap 3 Inventory");
        Assert.assertNotNull(returnType);

        ReleasesAndTransfers releasesAndTransfers = releasesAndTransfersDao.getByName("Controlled Water");
        Assert.assertNotNull(releasesAndTransfers);

        Parameter parameter = parameterDao.getByName("Net Energy Input Other solid fuels");
        Assert.assertNotNull(parameter);

        Unit unit = unitDao.getByName("TJ");
        Assert.assertNotNull(unit);

        Pair<ControlledListsList, Result> result
                = dependencyValidation.validate(returnType, releasesAndTransfers, parameter, unit);

        Assert.assertEquals(Pair.of(ControlledListsList.RELEASES_AND_TRANSFERS,
                Result.NOT_EXPECTED), result);
    }

    @Test
    public void nothing() {
        Pair<ControlledListsList, Result> result
                = dependencyValidation.validate(null, null, null, null);

        // We don't need to check the level (terminating entity for the happy path
        Assert.assertEquals(Pair.of(ControlledListsList.RETURN_TYPE,
                Result.EXPECTED), result);
    }

    /*
     * Test the hierarchy traverse and listing
     */
    @Test
    public void traverseReturningNullNoReleasesAndTransfers() {
        ReturnType returnType = returnTypeDao.getByName("Pollution Inventory");
        Assert.assertNotNull(returnType);

        Parameter parameter = parameterDao.getByName("Alachlor");
        Assert.assertNotNull(parameter);

        Pair<ControlledListsList, List<? extends DependentEntity>> result = dependencyNavigation.traverseHierarchy(returnType, parameter);

        Assert.assertNull(result.getRight());
        Assert.assertEquals(ControlledListsList.RELEASES_AND_TRANSFERS, result.getLeft());
    }

    @Test
    public void traverseReturningReleasesAndTransfersDao() {
        ReturnType returnType = returnTypeDao.getByName("Pollution Inventory");
        Assert.assertNotNull(returnType);

        Pair<ControlledListsList, List<? extends DependentEntity>> result = dependencyNavigation.traverseHierarchy(returnType);

        Assert.assertNotNull(result.getRight().size());
        Assert.assertEquals(ControlledListsList.RELEASES_AND_TRANSFERS, result.getLeft());

        printList(result.getRight());
    }

    @Test
    public void traverseReturningPollutionInventoryParameters() {
        ReturnType returnType = returnTypeDao.getByName("Pollution Inventory");
        Assert.assertNotNull(returnType);

        ReleasesAndTransfers releasesAndTransfers = releasesAndTransfersDao.getByName("Air");
        Assert.assertNotNull(releasesAndTransfers);

        Pair<ControlledListsList, List<? extends DependentEntity>> result = dependencyNavigation.traverseHierarchy(returnType, releasesAndTransfers);

        Assert.assertNotNull(result.getRight().size());
        Assert.assertEquals(ControlledListsList.PARAMETERS, result.getLeft());

        printList(result.getRight());
    }

    @Test
    public void traverseReturningLandfillAllParameters() {
        ReturnType returnType = returnTypeDao.getByName("Emissions to sewer");
        Assert.assertNotNull(returnType);

        Pair<ControlledListsList, List<? extends DependentEntity>> result = dependencyNavigation.traverseHierarchy(returnType);

        Assert.assertNotNull(result.getRight().size());
        Assert.assertEquals(ControlledListsList.PARAMETERS, result.getLeft());

        printList(result.getRight());
    }

    @Test
    public void traverseReturningLandfillAllUnits() {
        ReturnType returnType = returnTypeDao.getByName("Emissions to sewer");
        Assert.assertNotNull(returnType);

        Parameter parameter = parameterDao.getByName("Ziram");
        Assert.assertNotNull(parameter);

        Pair<ControlledListsList, List<? extends DependentEntity>> result = dependencyNavigation.traverseHierarchy(returnType, parameter);

        Assert.assertNotNull(result.getRight().size());
        Assert.assertEquals(ControlledListsList.UNITS, result.getLeft());

        printList(result.getRight());
    }

    @Test
    public void traverseReturningPollutionInventoryUnits() {
        ReturnType returnType = returnTypeDao.getByName("Pollution Inventory");
        Assert.assertNotNull(returnType);

        ReleasesAndTransfers releasesAndTransfers = releasesAndTransfersDao.getByName("Air");
        Assert.assertNotNull(releasesAndTransfers);

        Parameter parameter = parameterDao.getByName("Tritium");
        Assert.assertNotNull(parameter);

        Pair<ControlledListsList, List<? extends DependentEntity>> result = dependencyNavigation.traverseHierarchy(returnType, releasesAndTransfers, parameter);

        Assert.assertNotNull(result.getRight().size());
        Assert.assertEquals(ControlledListsList.UNITS, result.getLeft());

        printList(result.getRight());
    }

    @Test
    public void traverseReturningNullForLandfillWithReleasesAndTransfers() {
        ReturnType returnType = returnTypeDao.getByName("Emissions to sewer");
        Assert.assertNotNull(returnType);

        ReleasesAndTransfers releasesAndTransfers = releasesAndTransfersDao.getByName("Air");
        Assert.assertNotNull(releasesAndTransfers);

        Pair<ControlledListsList, List<? extends DependentEntity>> result = dependencyNavigation.traverseHierarchy(returnType, releasesAndTransfers);

        Assert.assertNull(result.getRight());
        Assert.assertEquals(result.getLeft(), ControlledListsList.RELEASES_AND_TRANSFERS);
    }

    @Test
    public void traverseReturningNullForExcludedParameter() {
        ReturnType returnType = returnTypeDao.getByName("Emissions to groundwater");
        Assert.assertNotNull(returnType);

        ReleasesAndTransfers releasesAndTransfers = releasesAndTransfersDao.getByName("Air");
        Assert.assertNotNull(releasesAndTransfers);

        Parameter parameter = parameterDao.getByName("Dichlorvos");
        Assert.assertNotNull(parameter);

        Pair<ControlledListsList, List<? extends DependentEntity>> result = dependencyNavigation.traverseHierarchy(returnType, releasesAndTransfers, parameter);

        Assert.assertNull(result.getRight());
        Assert.assertEquals(ControlledListsList.RELEASES_AND_TRANSFERS, result.getLeft());
    }

    @Test
    public void traverseReturningNullForExcludedParameterAndDisallowedReleases() {
        ReturnType returnType = returnTypeDao.getByName("Emissions to groundwater");
        Assert.assertNotNull(returnType);

        Parameter parameter = parameterDao.getByName("Dichlorvos");
        Assert.assertNotNull(parameter);

        Pair<ControlledListsList, List<? extends DependentEntity>> result = dependencyNavigation.traverseHierarchy(returnType, parameter);

        Assert.assertNull(result.getRight());
        Assert.assertEquals(ControlledListsList.PARAMETERS, result.getLeft());
    }

    @Test
    public void traverseReturningREMAllParameters() {
        ReturnType returnType = returnTypeDao.getByName("REM Return");
        Assert.assertNotNull(returnType);

        Pair<ControlledListsList, List<? extends DependentEntity>> result = dependencyNavigation.traverseHierarchy(returnType);

        Assert.assertNotNull(result.getRight().size());
        Assert.assertEquals(ControlledListsList.PARAMETERS, result.getLeft());

        printList(result.getRight());
    }

    @Test
    public void traverseReturningREMAllParametersAndUnits() {
        ReturnType returnType = returnTypeDao.getByName("REM Return");
        Assert.assertNotNull(returnType);

        Pair<ControlledListsList, List<? extends DependentEntity>> result = dependencyNavigation.traverseHierarchy(returnType);

        Assert.assertNotNull(result.getRight().size());
        Assert.assertEquals(ControlledListsList.PARAMETERS, result.getLeft());

        for (DependentEntity item : result.getRight()) {
            if (item instanceof Parameter) {
                Pair<ControlledListsList, List<? extends DependentEntity>> result2 = dependencyNavigation.traverseHierarchy(returnType, (Parameter)item);
                Assert.assertEquals(ControlledListsList.UNITS, result2.getLeft());
                Assert.assertEquals(1, result2.getRight().size());
                LOGGER.info(item.getName() + " " + result2.getRight().get(0).getName());
            }
        }

    }

    @Test
    public void traverseReturningREMReturnNullForLandfillParameter() {
        ReturnType returnType = returnTypeDao.getByName("REM Return");
        Assert.assertNotNull(returnType);

        // This parameter is not in the REM returns
        Parameter parameter = parameterDao.getByName("Methylparathion");
        Assert.assertNotNull(parameter);

        Pair<ControlledListsList, List<? extends DependentEntity>> result = dependencyNavigation.traverseHierarchy(returnType, parameter);

        // It might be expected that we return the null list at the units level
        // as this is what we have asked for. However because of the way the dependencies
        // are processed, in order to search the next level down there must be a path to it - i.e. a
        // wildcard or an explicit item. Here we find no route to let us search the
        // units cache hence the null cache is displayed at the parameter level.
        Assert.assertNull(result.getRight());
        Assert.assertEquals(ControlledListsList.PARAMETERS, result.getLeft());

    }

    @Test
    public void traverseAllNullsReturnsReturnTypes() {
        Pair<ControlledListsList, List<? extends DependentEntity>> result = dependencyNavigation.traverseHierarchy(null);

        Assert.assertNotNull(result.getRight().size());
        Assert.assertEquals(ControlledListsList.RETURN_TYPE, result.getLeft());

        printList(result.getRight());
    }

    private void printList(List<? extends DependentEntity> list) {
        for(DependentEntity e: list) {
            LOGGER.info(e.getName());
        }
    }
}
