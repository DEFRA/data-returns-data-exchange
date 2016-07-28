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
    MonitoringPeriodDao monitoringPeriodDao;

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
        // Add and retrieve
        MethodOrStandard methodOrStandard = new MethodOrStandard();
        methodOrStandard.setName("Test");
        methodOrStandardDao.add(methodOrStandard);
        MethodOrStandard retrieveReturnType = (MethodOrStandard) methodOrStandardDao.getByName("Test");
        Assert.assertNotNull(retrieveReturnType.getId());
        MethodOrStandard retrieveReturnType2 = (MethodOrStandard) methodOrStandardDao.getById(retrieveReturnType.getId());
        Assert.assertNotNull(retrieveReturnType2);
        Assert.assertEquals(retrieveReturnType2.getId(), retrieveReturnType.getId());
        methodOrStandardDao.removeById(retrieveReturnType2.getId());
        MethodOrStandard retrieveReturnType3 = (MethodOrStandard) methodOrStandardDao.getById(retrieveReturnType.getId());
        Assert.assertNull(retrieveReturnType3);
    }

    @Test
    public void testMonitoringPeriod() {
        // Add and retrieve
        MonitoringPeriod monitoringPeriod = new MonitoringPeriod();
        monitoringPeriod.setName("Test");
        monitoringPeriodDao.add(monitoringPeriod);
        MonitoringPeriod retrieveReturnType = (MonitoringPeriod) monitoringPeriodDao.getByName("Test");
        Assert.assertNotNull(retrieveReturnType.getId());
        MonitoringPeriod retrieveReturnType2 = (MonitoringPeriod) monitoringPeriodDao.getById(retrieveReturnType.getId());
        Assert.assertNotNull(retrieveReturnType2);
        Assert.assertEquals(retrieveReturnType2.getId(), retrieveReturnType.getId());
        monitoringPeriodDao.removeById(retrieveReturnType2.getId());
        MonitoringPeriod retrieveReturnType3 = (MonitoringPeriod) monitoringPeriodDao.getById(retrieveReturnType.getId());
        Assert.assertNull(retrieveReturnType3);
    }

    @Test
    public void testParameter() {
        // Add and retrieve
        Parameter parameter = new Parameter();
        parameter.setName("Test");
        parameterDao.add(parameter);
        Parameter retrieveReturnType = (Parameter) parameterDao.getByName("Test");
        Assert.assertNotNull(retrieveReturnType.getId());
        Parameter retrieveReturnType2 = (Parameter) parameterDao.getById(retrieveReturnType.getId());
        Assert.assertNotNull(retrieveReturnType2);
        Assert.assertEquals(retrieveReturnType2.getId(), retrieveReturnType.getId());
        parameterDao.removeById(retrieveReturnType2.getId());
        Parameter retrieveReturnType3 = (Parameter) parameterDao.getById(retrieveReturnType.getId());
        Assert.assertNull(retrieveReturnType3);
    }

    @Test
    public void testReturnType() {
        // Add and retrieve
        ReturnType returnType = new ReturnType();
        returnType.setName("Test");
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
}

