package uk.gov.defra.datareturns.tests.suite;

import junit.framework.JUnit4TestAdapter;
import junit.framework.TestSuite;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.junit.Test;
import org.junit.internal.runners.SuiteMethod;
import org.junit.runner.Description;
import org.junit.runner.Result;
import org.junit.runner.RunWith;
import org.junit.runner.notification.Failure;
import org.junit.runner.notification.RunListener;
import org.junit.runner.notification.RunNotifier;
import org.junit.runners.Suite;
import org.junit.runners.model.InitializationError;
import org.springframework.core.type.classreading.MetadataReader;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import uk.gov.defra.datareturns.util.Environment;

import java.util.function.Predicate;

@Slf4j
//@RunWith(AllTests.class)
@RunWith(AllUnitTests.TestRunner.class)
public class AllUnitTests {

    public AllUnitTests() {

    }

    public static TestSuite suite() {
        TestSuite suite = new TestSuite("Unit Tests");
        Predicate<MetadataReader> predicate = meta ->
                CollectionUtils.isNotEmpty(meta.getAnnotationMetadata().getAnnotatedMethods(Test.class.getName()));
        Environment.findClasses("uk.gov.defra.datareturns.tests.unit", predicate).stream()
                .map(JUnit4TestAdapter::new)
                .forEach(suite::addTest);
        return suite;
    }


    public static class TestRunner extends SuiteMethod {
        public TestRunner(Class<?> klass) throws Throwable {
            super(klass);
        }

        @Override
        public void run(RunNotifier notifier) {
            notifier.addListener(new ProjectTestRunListener());
            notifier.fireTestRunStarted(getDescription());
            super.run(notifier);
        }
    }

    @Slf4j
    public static class ProjectTestRunListener extends RunListener {
        @Override
        public void testRunStarted(Description description) throws Exception {
            log.info("Test Run Started " + description.getDisplayName());
        }

        @Override
        public void testRunFinished(Result result) throws Exception {
            log.info("Test Run Finished " + result.getRunCount());
        }

        @Override
        public void testStarted(Description description) throws Exception {
            log.info("Test Started " + description.getDisplayName());
        }

        @Override
        public void testFinished(Description description) throws Exception {
            log.info("Test Finished " + description.getDisplayName());
        }

        @Override
        public void testFailure(Failure failure) throws Exception {
            log.info("Test failed " + failure.getMessage());
        }

        @Override
        public void testAssumptionFailure(Failure failure) {
            log.info("Test assertion failed " + failure.getMessage());
        }

        @Override
        public void testIgnored(Description description) throws Exception {
            log.info("Test ignored " + description.getDisplayName());
        }
    }
}