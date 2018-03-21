package uk.gov.defra.datareturns.tests.suite;

import junit.framework.JUnit4TestAdapter;
import junit.framework.TestSuite;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.AllTests;
import org.springframework.core.type.classreading.MetadataReader;
import uk.gov.defra.datareturns.util.Environment;

import java.util.function.Predicate;

@SuppressWarnings("NonFinalUtilityClass")
@Slf4j
@RunWith(AllTests.class)
public class AllUnitTests {
    public AllUnitTests() {

    }

    public static TestSuite suite() {
        final TestSuite suite = new TestSuite("Unit Tests");
        final Predicate<MetadataReader> predicate = meta ->
                CollectionUtils.isNotEmpty(meta.getAnnotationMetadata().getAnnotatedMethods(Test.class.getName()));
        Environment.findClasses("uk.gov.defra.datareturns.tests.unit", predicate).stream()
                .map(JUnit4TestAdapter::new)
                .forEach(suite::addTest);
        return suite;
    }
}
