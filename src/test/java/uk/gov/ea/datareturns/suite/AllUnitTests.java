package uk.gov.ea.datareturns.suite;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;

import uk.gov.ea.datareturns.type.FileTypeTests;
import uk.gov.ea.datareturns.unittests.DateFormatTests;
import uk.gov.ea.datareturns.unittests.EmmaDatabaseTests;
import uk.gov.ea.datareturns.unittests.LocalStorageProviderTests;
import uk.gov.ea.datareturns.unittests.MonitorProEmailerTests;

@RunWith(Suite.class)
@SuiteClasses({
		EmmaDatabaseTests.class,
		FileTypeTests.class,
		DateFormatTests.class,
		LocalStorageProviderTests.class,
		MonitorProEmailerTests.class
})
public class AllUnitTests {
}
