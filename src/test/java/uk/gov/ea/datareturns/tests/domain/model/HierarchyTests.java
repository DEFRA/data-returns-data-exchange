package uk.gov.ea.datareturns.tests.domain.model;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.junit4.SpringRunner;
import uk.gov.ea.datareturns.App;
import uk.gov.ea.datareturns.domain.jpa.entities.Parameter;
import uk.gov.ea.datareturns.domain.jpa.entities.ReleasesAndTransfers;
import uk.gov.ea.datareturns.domain.jpa.entities.ReturnType;
import uk.gov.ea.datareturns.domain.jpa.entities.Unit;
import uk.gov.ea.datareturns.domain.jpa.entities.hierarchy.HierarchyNode;
import uk.gov.ea.datareturns.domain.jpa.entities.hierarchy.ParameterHierarchy;

import javax.inject.Inject;

/**
 * Created by graham on 17/11/16.
 */
@SpringBootTest(classes=App.class)
@DirtiesContext
@RunWith(SpringRunner.class)
public class HierarchyTests {

    @Inject
    ParameterHierarchy parameterHierarchy;

    @Test
    public void temp() {
        HierarchyNode rootNode = parameterHierarchy.root();
        Assert.assertEquals(rootNode.getHierarchyEntity(), ReturnType.class);
        rootNode = parameterHierarchy.next();
        Assert.assertEquals(rootNode.getHierarchyEntity(), ReleasesAndTransfers.class);
        rootNode = parameterHierarchy.next();
        Assert.assertEquals(rootNode.getHierarchyEntity(), Parameter.class);
        rootNode = parameterHierarchy.next();
        Assert.assertEquals(rootNode.getHierarchyEntity(), Unit.class);
    }


}
