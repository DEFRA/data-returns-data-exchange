package uk.gov.ea.datareturns.suite;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;

import uk.gov.ea.datareturns.helper.DataExchangeHelperTests;
import uk.gov.ea.datareturns.type.FileTypeTests;
import uk.gov.ea.datareturns.unittests.DateFormatTests;

@RunWith(Suite.class)
@SuiteClasses({
	DataExchangeHelperTests.class, 
	FileTypeTests.class,
	DateFormatTests.class
})
public class AllUnitTests
{
}
