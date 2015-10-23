package uk.gov.ea.datareturns.suite;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;

import uk.gov.ea.datareturns.SubmitReturnsIntegrationTests;

@RunWith(Suite.class)
@SuiteClasses(
{ SubmitReturnsIntegrationTests.class })
public class AllIntegrationTests
{

}
