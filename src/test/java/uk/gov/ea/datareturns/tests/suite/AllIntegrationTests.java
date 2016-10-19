package uk.gov.ea.datareturns.tests.suite;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;
import uk.gov.ea.datareturns.tests.unittests.DataSampleValidatorTests;
import uk.gov.ea.datareturns.tests.resource.ProcessorIntegrationTests;
import uk.gov.ea.datareturns.tests.resource.ResourceIntegrationTests;

@RunWith(Suite.class)
@SuiteClasses({
        ResourceIntegrationTests.class,
        ProcessorIntegrationTests.class
})
public class AllIntegrationTests {

}
