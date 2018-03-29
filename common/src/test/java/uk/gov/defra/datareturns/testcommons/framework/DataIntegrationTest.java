package uk.gov.defra.datareturns.testcommons.framework;

import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.FilterType;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;
import uk.gov.defra.datareturns.config.SpringDataConfiguration;
import uk.gov.defra.datareturns.config.ValidatorConfiguration;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Inherited
@Import({SpringDataConfiguration.class, ValidatorConfiguration.class})
@DataJpaTest(
        includeFilters = {
                @ComponentScan.Filter(type = FilterType.REGEX, pattern = "uk.gov.defra.datareturns.*")
        },
        excludeFilters = {
                @ComponentScan.Filter(type = FilterType.REGEX, pattern = "uk.gov.defra.datareturns.tests.*")
        },
        showSql = false
)
@ActiveProfiles("unit-test")
public @interface DataIntegrationTest {
}
