package uk.gov.defra.datareturns.tests.unit;

import lombok.extern.slf4j.Slf4j;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.junit4.SpringRunner;
import uk.gov.defra.datareturns.data.model.parameter.Parameter;
import uk.gov.defra.datareturns.data.model.parameter.ParameterRepository;
import uk.gov.defra.datareturns.data.model.parameter.ParameterType;
import uk.gov.defra.datareturns.data.model.parameter.ParameterTypeRepository;
import uk.gov.defra.datareturns.testcommons.framework.DataIntegrationTest;

import javax.inject.Inject;

@RunWith(SpringRunner.class)
@DataIntegrationTest
@WithMockUser(roles = {"ADMIN", "USER"})
@Slf4j
public class RepositoryTests {
    @Inject
    private ParameterRepository parameterRepository;
    @Inject
    private ParameterTypeRepository parameterTypeRepository;

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
