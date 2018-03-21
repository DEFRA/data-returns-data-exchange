package uk.gov.defra.datareturns.tests.unit;

import lombok.extern.slf4j.Slf4j;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.FilterType;
import org.springframework.context.annotation.Import;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;
import uk.gov.defra.datareturns.config.SpringDataConfiguration;
import uk.gov.defra.datareturns.data.loader.DataLoader;
import uk.gov.defra.datareturns.data.model.parameter.Parameter;
import uk.gov.defra.datareturns.data.model.parameter.ParameterRepository;
import uk.gov.defra.datareturns.data.model.parameter.ParameterType;
import uk.gov.defra.datareturns.data.model.parameter.ParameterTypeRepository;

import javax.inject.Inject;

@RunWith(SpringRunner.class)
@Import(SpringDataConfiguration.class)
@DataJpaTest(
        includeFilters = {
                @ComponentScan.Filter(type = FilterType.REGEX, pattern = "uk.gov.defra.datareturns.*")
        },
        excludeFilters = {
                @ComponentScan.Filter(type = FilterType.REGEX, pattern = "uk.gov.defra.datareturns.tests.*")
        },
        showSql = false
)
@ActiveProfiles("unit-test")
@WithMockUser(roles = {"ADMIN", "USER"})
@Slf4j
public class RepositoryTests {
    private static boolean dbInitialised = false;
    @Inject
    private DataLoader loader;
    @Inject
    private ParameterRepository parameterRepository;
    @Inject
    private ParameterTypeRepository parameterTypeRepository;

    @Before
    public void setupDb() {
        if (!dbInitialised) {
            dbInitialised = true;
            loader.loadAll();
        }
    }

    @Test
    public void testAddParameter() {
        final ParameterType pt = parameterTypeRepository.getOne(1L);
        final Parameter p = new Parameter();
        p.setNomenclature("Test");
        p.setCas("Test");
        p.setType(pt);
        parameterRepository.saveAndFlush(p);
    }

    @Test(expected = DataIntegrityViolationException.class)
    public void testDuplicateFailure() {
        final ParameterType pt = parameterTypeRepository.getOne(1L);

        final Parameter p1 = new Parameter();
        p1.setNomenclature("Test Duplicate");
        p1.setType(pt);
        parameterRepository.saveAndFlush(p1);

        final Parameter p2 = new Parameter();
        p2.setNomenclature("Test Duplicate");
        p2.setType(pt);
        parameterRepository.saveAndFlush(p2);
    }
}
