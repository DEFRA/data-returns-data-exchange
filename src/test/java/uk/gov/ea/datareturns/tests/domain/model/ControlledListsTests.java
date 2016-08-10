package uk.gov.ea.datareturns.tests.domain.model;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.ConfigFileApplicationContextInitializer;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import uk.gov.ea.datareturns.App;
import uk.gov.ea.datareturns.domain.jpa.dao.*;
import uk.gov.ea.datareturns.domain.jpa.entities.*;

import javax.inject.Inject;

/**
 * Created by graham on 28/07/16.
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = App.class, initializers = ConfigFileApplicationContextInitializer.class)
public class ControlledListsTests {

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

    @Test
    public void testMethodOrStandard() {
        MethodOrStandard methodOrStandard0 = (MethodOrStandard) methodOrStandardDao.getByName("Test");
        if (methodOrStandard0 != null) {
            methodOrStandardDao.removeById(methodOrStandard0.getId());
        }
        // Add and retrieve
        MethodOrStandard methodOrStandard = new MethodOrStandard();
        methodOrStandard.setName("Test");
        methodOrStandardDao.add(methodOrStandard);
        MethodOrStandard retrieveMethodOrStandard = (MethodOrStandard) methodOrStandardDao.getByName("Test");
        Assert.assertNotNull(retrieveMethodOrStandard.getId());
        MethodOrStandard retrieveMethodOrStandard2 = (MethodOrStandard) methodOrStandardDao.getById(retrieveMethodOrStandard.getId());
        Assert.assertNotNull(retrieveMethodOrStandard2);
        Assert.assertEquals(retrieveMethodOrStandard2.getId(), retrieveMethodOrStandard.getId());
        methodOrStandardDao.removeById(retrieveMethodOrStandard2.getId());
        MethodOrStandard retrieveMethodOrStandard3 = (MethodOrStandard) methodOrStandardDao.getById(retrieveMethodOrStandard.getId());
        Assert.assertNull(retrieveMethodOrStandard3);
    }

    @Test
    public void testParameter() {
        Parameter retrieveParameter0 = (Parameter) parameterDao.getByName("Test");
        if (retrieveParameter0 != null) {
            parameterDao.removeById(retrieveParameter0.getId());
        }
        // Add and retrieve
        Parameter parameter = new Parameter();
        parameter.setName("Test");
        parameterDao.add(parameter);
        Parameter retrieveParameter = (Parameter) parameterDao.getByName("Test");
        Assert.assertNotNull(retrieveParameter.getId());
        Parameter retrieveParameter2 = (Parameter) parameterDao.getById(retrieveParameter.getId());
        Assert.assertNotNull(retrieveParameter2);
        Assert.assertEquals(retrieveParameter2.getId(), retrieveParameter.getId());
        parameterDao.removeById(retrieveParameter2.getId());
        Parameter retrieveParameter3 = (Parameter) parameterDao.getById(retrieveParameter.getId());
        Assert.assertNull(retrieveParameter3);
    }

    @Test
    public void testReturnType() {
        // Make the test type is gone
        ReturnType retrieveReturnType0 = (ReturnType) returnTypeDao.getByName("Test");
        if (retrieveReturnType0 != null) {
            returnTypeDao.removeById(retrieveReturnType0.getId());
        }
        
        // Add and retrieve
        ReturnType returnType = new ReturnType();
        returnType.setName("Test");
        returnType.setSector("Sector");
        returnTypeDao.add(returnType);
        ReturnType retrieveReturnType = (ReturnType) returnTypeDao.getByName("Test");
        Assert.assertNotNull(retrieveReturnType.getId());
        ReturnType retrieveReturnType2 = (ReturnType) returnTypeDao.getById(retrieveReturnType.getId());
        Assert.assertNotNull(retrieveReturnType2);
        Assert.assertEquals(retrieveReturnType2.getId(), retrieveReturnType.getId());
        returnTypeDao.removeById(retrieveReturnType2.getId());
        ReturnType retrieveReturnType3 = (ReturnType) returnTypeDao.getById(retrieveReturnType.getId());
        Assert.assertNull(retrieveReturnType3);
    }

    @Test
    public void testUnit() {
        // Make the test type is gone
        Unit retrieveUnit0 = (Unit) unitDao.getByName("Test");
        if (retrieveUnit0 != null) {
            unitDao.removeById(retrieveUnit0.getId());
        }
        // Add and retrieve
        Unit unit = new Unit();
        unit.setName("Test");
        unit.setDescription("Test");
        unit.setMeasureType("Test");
        unitDao.add(unit);
        Unit retrieveUnit = (Unit) unitDao.getByName("Test");
        Assert.assertNotNull(retrieveUnit.getId());
        Unit retrieveUnit2 = (Unit) unitDao.getById(retrieveUnit.getId());
        Assert.assertNotNull(retrieveUnit2);
        Assert.assertEquals(retrieveUnit2.getId(), retrieveUnit.getId());
        unitDao.removeById(retrieveUnit2.getId());
        Unit retrieveUnit3 = (Unit) unitDao.getById(retrieveUnit.getId());
        Assert.assertNull(retrieveUnit3);
    }

    @Test
    public void testQualifier() {
        // Make the test type is gone
        Qualifier retrieveQualifier0 = (Qualifier) qualifierDao.getByName("Test");
        if (retrieveQualifier0 != null) {
            qualifierDao.removeById(retrieveQualifier0.getId());
        }
        // Add and retrieve
        Qualifier Qualifier = new Qualifier();
        Qualifier.setName("Test");
        Qualifier.setDescription("Test");
        qualifierDao.add(Qualifier);
        Qualifier retrieveQualifier = (Qualifier) qualifierDao.getByName("Test");
        Assert.assertNotNull(retrieveQualifier.getId());
        Qualifier retrieveQualifier2 = (Qualifier) qualifierDao.getById(retrieveQualifier.getId());
        Assert.assertNotNull(retrieveQualifier2);
        Assert.assertEquals(retrieveQualifier2.getId(), retrieveQualifier.getId());
        qualifierDao.removeById(retrieveQualifier2.getId());
        Qualifier retrieveQualifier3 = (Qualifier) qualifierDao.getById(retrieveQualifier.getId());
        Assert.assertNull(retrieveQualifier3);
    }
}

