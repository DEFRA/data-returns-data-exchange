package uk.gov.ea.datareturns.tests.suite;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;

import uk.gov.ea.datareturns.tests.domain.model.MonitoringDataRecordValidatorTests;
import uk.gov.ea.datareturns.tests.resource.ProcessorIntegrationTests;
import uk.gov.ea.datareturns.tests.resource.ResourceIntegrationTests;

@RunWith(Suite.class)
@SuiteClasses({
		MonitoringDataRecordValidatorTests.class,
		ResourceIntegrationTests.class,
		ProcessorIntegrationTests.class
})
public class AllIntegrationTests {

}
