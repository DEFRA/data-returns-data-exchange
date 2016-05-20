package uk.gov.ea.datareturns.tests.suite;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;

import uk.gov.ea.datareturns.tests.type.FileTypeTests;
import uk.gov.ea.datareturns.tests.unittests.BooleanValueTests;
import uk.gov.ea.datareturns.tests.unittests.DataReturnsHeadersTests;
import uk.gov.ea.datareturns.tests.unittests.DataReturnsZipFileModelTests;
import uk.gov.ea.datareturns.tests.unittests.DateFormatTests;
import uk.gov.ea.datareturns.tests.unittests.EaIdTypeTests;
import uk.gov.ea.datareturns.tests.unittests.LocalStorageProviderTests;
import uk.gov.ea.datareturns.tests.unittests.MonitorProEmailerTests;
import uk.gov.ea.datareturns.tests.unittests.S3StorageConfigurationTests;
import uk.gov.ea.datareturns.tests.unittests.StorageHealthCheckTests;

@RunWith(Suite.class)
@SuiteClasses({
		BooleanValueTests.class,
		DataReturnsHeadersTests.class,
		DataReturnsZipFileModelTests.class,
		DateFormatTests.class,
		EaIdTypeTests.class,
		LocalStorageProviderTests.class,
		MonitorProEmailerTests.class,
		S3StorageConfigurationTests.class,
		StorageHealthCheckTests.class,
		FileTypeTests.class
})
public class AllUnitTests {
}
