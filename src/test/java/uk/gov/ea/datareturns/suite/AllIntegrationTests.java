package uk.gov.ea.datareturns.suite;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;

import uk.gov.ea.datareturns.domain.model.MonitoringDataRecordValidatorTests;
import uk.gov.ea.datareturns.resource.ResourceIntegrationTests;

@RunWith(Suite.class)
@SuiteClasses({
		MonitoringDataRecordValidatorTests.class,
		ResourceIntegrationTests.class
})
public class AllIntegrationTests {

}
