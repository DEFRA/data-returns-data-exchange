package uk.gov.ea.datareturns.suite;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;

import uk.gov.ea.datareturns.type.FileTypeTests;
import uk.gov.ea.datareturns.unittests.BooleanValueTests;
import uk.gov.ea.datareturns.unittests.DataReturnsZipFileModelTests;
import uk.gov.ea.datareturns.unittests.DateFormatTests;
import uk.gov.ea.datareturns.unittests.EaIdTypeTests;
import uk.gov.ea.datareturns.unittests.LocalStorageProviderTests;
import uk.gov.ea.datareturns.unittests.MonitorProEmailerTests;
import uk.gov.ea.datareturns.unittests.StorageHealthCheckTests;

@RunWith(Suite.class)
@SuiteClasses({
		BooleanValueTests.class,
		DataReturnsZipFileModelTests.class,
		DateFormatTests.class,
		EaIdTypeTests.class,
		LocalStorageProviderTests.class,
		MonitorProEmailerTests.class,
		StorageHealthCheckTests.class,
		FileTypeTests.class
})
public class AllUnitTests {
}
