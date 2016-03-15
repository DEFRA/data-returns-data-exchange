package uk.gov.ea.datareturns.suite;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;

import uk.gov.ea.datareturns.helper.CommonHelperTests;
import uk.gov.ea.datareturns.helper.DataExchangeHelperTests;
import uk.gov.ea.datareturns.helper.FileUtilsHelperTests;
import uk.gov.ea.datareturns.type.EnvironmentTypeTests;
import uk.gov.ea.datareturns.type.FileTypeTests;

@RunWith(Suite.class)
@SuiteClasses(
{ 	CommonHelperTests.class,
	DataExchangeHelperTests.class, 
	FileUtilsHelperTests.class, 
	FileTypeTests.class,
	EnvironmentTypeTests.class})
public class AllUnitTests
{
}
