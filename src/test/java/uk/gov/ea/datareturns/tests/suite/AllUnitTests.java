package uk.gov.ea.datareturns.tests.suite;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;
import uk.gov.ea.datareturns.tests.domain.model.ControlledListsTests;
import uk.gov.ea.datareturns.tests.domain.model.DependenciesTests;
import uk.gov.ea.datareturns.tests.type.FileTypeTests;
import uk.gov.ea.datareturns.tests.unittests.*;

@RunWith(Suite.class)
@SuiteClasses({
        ControlledListsTests.class,
        DependenciesTests.class,

        DataQualityTests.class,
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
