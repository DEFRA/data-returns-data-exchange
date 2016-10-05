package uk.gov.ea.datareturns.tests.domain.model;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.junit4.SpringRunner;
import uk.gov.ea.datareturns.App;
import uk.gov.ea.datareturns.domain.jpa.dao.*;
import uk.gov.ea.datareturns.domain.jpa.entities.*;
import uk.gov.ea.datareturns.domain.jpa.service.DependencyValidation;

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
    DependencyValidation dependencyValidation;

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
    public void dependencyTestLandfillHappy1() {
        ReturnType returnType = returnTypeDao.getByName("Emissions to groundwater");
        Assert.assertNotNull(returnType);
        Parameter parameter = parameterDao.getByName("Trichlorobenzene");
        Assert.assertNotNull(parameter);
        DependencyValidation.DependencyValidationResult result
                = dependencyValidation.validate(returnType, parameter);
        Assert.assertEquals(DependencyValidation.DependencyValidationResult.OK, result);
    }

    /*
     * Test landfill happy paths - with unit
     */
    @Test
    public void dependencyTestLandfillHappy2() {
        ReturnType returnType = returnTypeDao.getByName("Emissions to groundwater");
        Assert.assertNotNull(returnType);
        Parameter parameter = parameterDao.getByName("Benzoic acid, p-tert-butyl");
        Assert.assertNotNull(parameter);
        Unit unit = unitDao.getByName("cm3/hr");
        Assert.assertNotNull(unit);
        DependencyValidation.DependencyValidationResult result
                = dependencyValidation.validate(returnType, parameter, unit);
        Assert.assertEquals(DependencyValidation.DependencyValidationResult.OK, result);
    }

    /*
     * With disallowed parameter
     */
    @Test
    public void dependencyTestLandfillWrongParameter() {
        ReturnType returnType = returnTypeDao.getByName("Ambient air quality");
        Assert.assertNotNull(returnType);
        Parameter parameter = parameterDao.getByName("Krypton 85");
        Assert.assertNotNull(parameter);
        Unit unit = unitDao.getByName("cm3/hr");
        Assert.assertNotNull(unit);
        DependencyValidation.DependencyValidationResult result
                = dependencyValidation.validate(returnType, parameter, unit);
        Assert.assertEquals(DependencyValidation.DependencyValidationResult.BAD_PARAMETER, result);
    }

    /*
     * With no parameter
     */
    @Test
    public void dependencyTestLandfillNoParameter() {
        ReturnType returnType = returnTypeDao.getByName("Ambient air quality");
        Assert.assertNotNull(returnType);
        Unit unit = unitDao.getByName("cm3/hr");
        Assert.assertNotNull(unit);
        DependencyValidation.DependencyValidationResult result
                = dependencyValidation.validate(returnType, new Parameter(), unit);
        Assert.assertEquals(DependencyValidation.DependencyValidationResult.NO_PARAMETER, result);
        result = dependencyValidation.validate(returnType, null, unit);
        Assert.assertEquals(DependencyValidation.DependencyValidationResult.NO_PARAMETER, result);
    }

    /*
     * The should be excluded by using  "^*" - none should be given
     */
    @Test
    public void dependencyTestLandfillSpecifyReleasesInError() {
        ReturnType returnType = returnTypeDao.getByName("Emissions to sewer");
        Assert.assertNotNull(returnType);
        Parameter parameter = parameterDao.getByName("Diethylenetriamine");
        Assert.assertNotNull(parameter);
        Unit unit = unitDao.getByName("µScm⁻¹");
        Assert.assertNotNull(unit);
        ReleasesAndTransfers releasesAndTransfers = releasesAndTransfersDao.getByName("Controlled Water");
        Assert.assertNotNull(releasesAndTransfers);
        DependencyValidation.DependencyValidationResult result
                = dependencyValidation.validate(returnType, releasesAndTransfers, parameter, unit);
        Assert.assertEquals(DependencyValidation.DependencyValidationResult.RELEASE_NOT_APPLICABLE, result);
    }


}
