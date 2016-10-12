package uk.gov.ea.datareturns.tests.domain.model;

import org.apache.commons.lang3.tuple.Pair;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
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
     * Test the reporting (displaying the published controlled lists
     * with resolved dependencies
     */
//    @Test
//    public void traverseError_1() {
//        Pair<ControlledListsList, List<DependentEntity>> result = dependencyNavigation.traverseHierarchy(new DependentEntity[] {});
//        Assert.assertEquals(result, null);
//    }

//    @Test
//    public void traverseError_2() {
//        ReturnType returnType = returnTypeDao.getByName("Pollution Inventory");
//        Assert.assertNotNull(returnType);
//
//        Parameter parameter = parameterDao.getByName("Alachlor");
//        Assert.assertNotNull(parameter);
//
//        ReleasesAndTransfers releasesAndTransfers = releasesAndTransfersDao.getByName("Controlled Water");
//        Assert.assertNotNull(releasesAndTransfers);
//
//        Unit unit = unitDao.getByName("TJ");
//
//        Pair<ControlledListsList, List<DependentEntity>> result = dependencyNavigation.traverseHierarchy(
//                new DependentEntity[] {returnType, parameter, releasesAndTransfers, unit});
//
//        Assert.assertEquals(result, null);
//    }

//    @Test
//    public void traverseReturnTypeReturningParameters_1() {
//        ReturnType returnType = returnTypeDao.getByName("Pollution Inventory");
//        Assert.assertNotNull(returnType);
//
//        ReleasesAndTransfers releasesAndTransfers = releasesAndTransfersDao.getByName("Air");
//        Assert.assertNotNull(releasesAndTransfers);
//
//        Pair<ControlledListsList, List<DependentEntity>> result = dependencyNavigation.traverseHierarchy(returnType, releasesAndTransfers);
//
//        Assert.assertNotNull(result.getRight().size());
//        Assert.assertEquals(result.getLeft(), ControlledListsList.UNITS);
//    }
//
//    @Test
//    public void traverseReturnTypeReturningLandfillParameters() {
//        ReturnType returnType = returnTypeDao.getByName("Emissions to groundwater");
//        Assert.assertNotNull(returnType);
//
//        Pair<ControlledListsList, List<DependentEntity>> result = dependencyNavigation.traverseHierarchy(returnType);
//
//        Assert.assertNotNull(result.getRight().size());
//        Assert.assertEquals(result.getLeft(), ControlledListsList.PARAMETERS);
//    }
}
