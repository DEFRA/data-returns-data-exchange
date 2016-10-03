package uk.gov.ea.datareturns.tests.domain.model;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import uk.gov.ea.datareturns.App;
import uk.gov.ea.datareturns.domain.jpa.dao.DependenciesDao;
import uk.gov.ea.datareturns.domain.jpa.entities.Dependencies;

import javax.inject.Inject;
import java.util.List;

/**
 * Created by graham on 03/10/16.
 */
@SpringBootTest(classes=App.class)
@RunWith(SpringRunner.class)
public class DependenciesTests {
    @Inject
    DependenciesDao dao;

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
    public void integrity() {
        Assert.assertTrue(dao.checkIntegrity());
    }

    @Test
    public void buildCache() {
        Assert.assertNotNull(dao.buildCache());
    }
}
