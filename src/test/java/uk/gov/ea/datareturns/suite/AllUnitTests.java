package uk.gov.ea.datareturns.suite;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;

import uk.gov.ea.datareturns.type.FileTypeTests;
import uk.gov.ea.datareturns.unittests.BooleanValueTests;
import uk.gov.ea.datareturns.unittests.DataReturnsZipFileModelTests;
import uk.gov.ea.datareturns.unittests.DateFormatTests;
import uk.gov.ea.datareturns.unittests.EmmaDatabaseTests;
import uk.gov.ea.datareturns.unittests.LocalStorageProviderTests;
import uk.gov.ea.datareturns.unittests.MonitorProEmailerTests;
import uk.gov.ea.datareturns.unittests.StorageHealthCheckTests;

@RunWith(Suite.class)
@SuiteClasses({
		DataReturnsZipFileModelTests.class,
		StorageHealthCheckTests.class,
		EmmaDatabaseTests.class,
		BooleanValueTests.class,
		FileTypeTests.class,
		DateFormatTests.class,
		LocalStorageProviderTests.class,
		MonitorProEmailerTests.class
})
public class AllUnitTests {
}
