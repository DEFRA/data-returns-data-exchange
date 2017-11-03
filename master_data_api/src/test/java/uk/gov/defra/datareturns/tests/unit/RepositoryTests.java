package uk.gov.defra.datareturns.tests.unit;

import lombok.extern.slf4j.Slf4j;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.FilterType;
import org.springframework.context.annotation.Import;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.test.context.junit4.SpringRunner;
import uk.gov.defra.datareturns.config.SpringDataConfiguration;
import uk.gov.defra.datareturns.data.loader.DatabaseLoader;
import uk.gov.defra.datareturns.data.model.parameter.Parameter;
import uk.gov.defra.datareturns.data.model.parameter.ParameterRepository;
import uk.gov.defra.datareturns.util.SpringApplicationContextProvider;

import javax.inject.Inject;
import java.util.Map;

@RunWith(SpringRunner.class)
@Import(SpringDataConfiguration.class)
@DataJpaTest(
        useDefaultFilters = false,
        includeFilters = {
                @ComponentScan.Filter(type = FilterType.ASSIGNABLE_TYPE, value = {SpringApplicationContextProvider.class, DatabaseLoader.class})
        },
        showSql = false
)
//@SpringBootTest(classes = {App.class}, webEnvironment = SpringBootTest.WebEnvironment.MOCK)
//@ActiveProfiles("IntegrationTests")
@Slf4j
public class RepositoryTests {
    @Inject
    Map<String, DatabaseLoader> loaderBeans;

    @Inject
    ParameterRepository parameterRepository;

    @Test
    public void testParameters() {
        loaderBeans.forEach((name, loader) -> {
            log.info("Executing base data loader: {}", name);
            loader.load();
        });
        Assert.assertTrue(parameterRepository.count() > 0);
    }

    @Test
    public void testAddParameter() {
        Parameter p = new Parameter();
        p.setNomenclature("Test");
        p.setCas("Test");
        parameterRepository.saveAndFlush(p);
    }

    @Test(expected = DataIntegrityViolationException.class)
    public void testDuplicateFailure() {
        Parameter p1 = new Parameter();
        p1.setNomenclature("Test Duplicate");
        parameterRepository.saveAndFlush(p1);

        Parameter p2 = new Parameter();
        p2.setNomenclature("Test Duplicate");
        parameterRepository.saveAndFlush(p2);
    }
}
