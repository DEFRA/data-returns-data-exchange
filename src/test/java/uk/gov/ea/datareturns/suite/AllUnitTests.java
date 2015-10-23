package uk.gov.ea.datareturns.suite;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;

import uk.gov.ea.datareturns.helper.CommonHelperTests;
import uk.gov.ea.datareturns.helper.DataExchangeHelperTests;

@RunWith(Suite.class)
@SuiteClasses(
{ CommonHelperTests.class, DataExchangeHelperTests.class })
public class AllUnitTests
{

}
