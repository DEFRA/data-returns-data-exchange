package uk.gov.ea.datareturns.suite;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;

import uk.gov.ea.datareturns.helper.CommonHelperTests;
import uk.gov.ea.datareturns.helper.DataExchangeHelperTests;
import uk.gov.ea.datareturns.helper.FileUtilsHelperTests;
import uk.gov.ea.datareturns.type.ApplicationExceptionTests;
import uk.gov.ea.datareturns.type.SystemExceptionTests;

@RunWith(Suite.class)
@SuiteClasses(
{ CommonHelperTests.class, DataExchangeHelperTests.class, ApplicationExceptionTests.class, SystemExceptionTests.class, FileUtilsHelperTests.class })
public class AllUnitTests
{

}
