package uk.gov.ea.datareturns.suite;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;

import uk.gov.ea.datareturns.resource.ResourceIntegrationTests;

@RunWith(Suite.class)
@SuiteClasses(
{ ResourceIntegrationTests.class })
public class AllIntegrationTests
{

}
