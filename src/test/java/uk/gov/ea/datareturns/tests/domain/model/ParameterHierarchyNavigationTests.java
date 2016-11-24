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
import uk.gov.ea.datareturns.domain.jpa.dao.ParameterDao;
import uk.gov.ea.datareturns.domain.jpa.dao.ReleasesAndTransfersDao;
import uk.gov.ea.datareturns.domain.jpa.dao.ReturnTypeDao;
import uk.gov.ea.datareturns.domain.jpa.dao.UnitDao;
import uk.gov.ea.datareturns.domain.jpa.entities.ControlledListsList;
import uk.gov.ea.datareturns.domain.jpa.entities.Parameter;
import uk.gov.ea.datareturns.domain.jpa.entities.ReleasesAndTransfers;
import uk.gov.ea.datareturns.domain.jpa.entities.ReturnType;
import uk.gov.ea.datareturns.domain.jpa.hierarchy.Hierarchy;
import uk.gov.ea.datareturns.domain.jpa.hierarchy.HierarchyLevel;
import uk.gov.ea.datareturns.domain.jpa.hierarchy.implementations.ParameterHierarchy;

import javax.inject.Inject;
import java.util.List;

/**
 * Created by graham on 17/11/16.
 */
@SpringBootTest(classes=App.class)
@DirtiesContext
@RunWith(SpringRunner.class)
public class ParameterHierarchyNavigationTests {
    protected static final Logger LOGGER = LoggerFactory.getLogger(ParameterHierarchyNavigationTests.class);

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

    @Test
    public void traverseReturningNullNoReleasesAndTransfers() {
        ReturnType returnType = returnTypeDao.getByName("Pollution Inventory");
        Assert.assertNotNull(returnType);

        ReleasesAndTransfers releasesAndTransfers = releasesAndTransfersDao.getByName("Air");
        Assert.assertNotNull(releasesAndTransfers);

        Pair<HierarchyLevel, List<? extends Hierarchy.HierarchyEntity>> result = parameterHierarchy.children(returnType, releasesAndTransfers);

        Assert.assertNotNull(result.getRight());
        Assert.assertEquals(result.getRight().get(1).getClass(), Parameter.class);
        Assert.assertEquals(ControlledListsList.PARAMETER, result.getLeft().getControlledList());
    }
    
    @Test
    public void traverseReturningReleasesAndTransfersDao() {
        ReturnType returnType = returnTypeDao.getByName("Pollution Inventory");
        Assert.assertNotNull(returnType);

        Pair<HierarchyLevel, List<? extends Hierarchy.HierarchyEntity>> result = parameterHierarchy.children(returnType);

        Assert.assertNotNull(result.getRight());
        Assert.assertEquals(ControlledListsList.RELEASES_AND_TRANSFER, result.getLeft().getControlledList());

        printList(result.getRight());
    }

    @Test
    public void traverseReturningPollutionInventoryParameters() {
        ReturnType returnType = returnTypeDao.getByName("Pollution Inventory");
        Assert.assertNotNull(returnType);

        ReleasesAndTransfers releasesAndTransfers = releasesAndTransfersDao.getByName("Air");
        Assert.assertNotNull(releasesAndTransfers);

        Pair<HierarchyLevel, List<? extends Hierarchy.HierarchyEntity>> result = parameterHierarchy.children(returnType, releasesAndTransfers);

        Assert.assertNotNull(result.getRight());
        Assert.assertEquals(ControlledListsList.PARAMETER, result.getLeft().getControlledList());

        printList(result.getRight());
    }

    @Test
    public void traverseReturningLandfillAllParameters() {
        ReturnType returnType = returnTypeDao.getByName("Emissions to sewer");
        Assert.assertNotNull(returnType);

        Pair<HierarchyLevel, List<? extends Hierarchy.HierarchyEntity>> result = parameterHierarchy.children(returnType);

        Assert.assertNotNull(result.getRight());
        Assert.assertEquals(ControlledListsList.PARAMETER, result.getLeft().getControlledList());

        printList(result.getRight());
    }

    @Test
    public void traverseReturningLandfillAllUnits() {
        ReturnType returnType = returnTypeDao.getByName("Emissions to sewer");
        Assert.assertNotNull(returnType);

        Parameter parameter = parameterDao.getByName("Ziram");
        Assert.assertNotNull(parameter);

        Pair<HierarchyLevel, List<? extends Hierarchy.HierarchyEntity>> result = parameterHierarchy.children(returnType, parameter);

        Assert.assertNotNull(result.getRight());
        Assert.assertEquals(ControlledListsList.UNIT, result.getLeft().getControlledList());

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

        Pair<HierarchyLevel, List<? extends Hierarchy.HierarchyEntity>> result = parameterHierarchy.children(returnType, releasesAndTransfers, parameter);

        Assert.assertNotNull(result.getRight());
        Assert.assertEquals(ControlledListsList.UNIT, result.getLeft().getControlledList());

        printList(result.getRight());
    }

    @Test
    public void traverseReturningNullForLandfillWithReleasesAndTransfers() {
        ReturnType returnType = returnTypeDao.getByName("Emissions to sewer");
        Assert.assertNotNull(returnType);

        ReleasesAndTransfers releasesAndTransfers = releasesAndTransfersDao.getByName("Air");
        Assert.assertNotNull(releasesAndTransfers);

        Pair<HierarchyLevel, List<? extends Hierarchy.HierarchyEntity>> result = parameterHierarchy.children(returnType, releasesAndTransfers);

        Assert.assertNull(result.getRight());
        Assert.assertEquals(ControlledListsList.RELEASES_AND_TRANSFER, result.getLeft().getControlledList());
    }

    @Test
    public void traverseReturningNullForExcludedParameter() {
        ReturnType returnType = returnTypeDao.getByName("Emissions to groundwater");
        Assert.assertNotNull(returnType);

        ReleasesAndTransfers releasesAndTransfers = releasesAndTransfersDao.getByName("Air");
        Assert.assertNotNull(releasesAndTransfers);

        Parameter parameter = parameterDao.getByName("Dichlorvos");
        Assert.assertNotNull(parameter);

        Pair<HierarchyLevel, List<? extends Hierarchy.HierarchyEntity>> result = parameterHierarchy.children(returnType, releasesAndTransfers, parameter);

        Assert.assertNull(result.getRight());
        Assert.assertEquals(ControlledListsList.RELEASES_AND_TRANSFER, result.getLeft().getControlledList());
    }

    @Test
    public void traverseReturningNullForExcludedParameterAndDisallowedReleases() {
        ReturnType returnType = returnTypeDao.getByName("Emissions to groundwater");
        Assert.assertNotNull(returnType);

        Parameter parameter = parameterDao.getByName("Dichlorvos");
        Assert.assertNotNull(parameter);

        Pair<HierarchyLevel, List<? extends Hierarchy.HierarchyEntity>> result = parameterHierarchy.children(returnType, parameter);

        Assert.assertNull(result.getRight());
        Assert.assertEquals(ControlledListsList.PARAMETER, result.getLeft().getControlledList());
    }

    @Test
    public void traverseReturningREMAllParameters() {
        ReturnType returnType = returnTypeDao.getByName("REM Return");
        Assert.assertNotNull(returnType);

        Pair<HierarchyLevel, List<? extends Hierarchy.HierarchyEntity>> result = parameterHierarchy.children(returnType);

        Assert.assertNotNull(result.getRight());
        Assert.assertEquals(ControlledListsList.PARAMETER, result.getLeft().getControlledList());

        printList(result.getRight());
    }

    @Test
    public void traverseReturningREMAllParametersAndUnits() {
        ReturnType returnType = returnTypeDao.getByName("REM Return");
        Assert.assertNotNull(returnType);

        Pair<HierarchyLevel, List<? extends Hierarchy.HierarchyEntity>> result = parameterHierarchy.children(returnType);

        Assert.assertNotNull(result.getRight());
        Assert.assertEquals(ControlledListsList.PARAMETER, result.getLeft().getControlledList());

        for (Hierarchy.HierarchyEntity item : result.getRight()) {
            if (item instanceof Parameter) {
                Pair<HierarchyLevel, List<? extends Hierarchy.HierarchyEntity>> result2 = parameterHierarchy.children(returnType, (Parameter)item);
                Assert.assertEquals(ControlledListsList.UNIT, result2.getLeft().getControlledList());
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

        Pair<HierarchyLevel, List<? extends Hierarchy.HierarchyEntity>> result = parameterHierarchy.children(returnType, parameter);

        // It might be expected that we return the null list at the units level
        // as this is what we have asked for. However because of the way the dependencies
        // are processed, in order to search the next level down there must be a path to it - i.e. a
        // wildcard or an explicit item. Here we find no route to let us search the
        // units cache hence the null cache is displayed at the parameter level.
        Assert.assertNull(result.getRight());
        Assert.assertEquals(ControlledListsList.PARAMETER, result.getLeft().getControlledList());

    }

    @Test
    public void traverseAllNullsReturnsReturnTypes() {
        Pair<HierarchyLevel, List<? extends Hierarchy.HierarchyEntity>> result = parameterHierarchy.children((Hierarchy.HierarchyEntity)null);

        Assert.assertNotNull(result.getRight());
        Assert.assertEquals(ControlledListsList.RETURN_TYPE, result.getLeft().getControlledList());

        printList(result.getRight());
    }

    private void printList(List<? extends Hierarchy.HierarchyEntity> list) {
        for(Hierarchy.HierarchyEntity e: list) {
            LOGGER.info(e.getName());
        }
    }


}