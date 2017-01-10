package uk.gov.ea.datareturns.tests.integration.metrics;

import org.assertj.core.api.Assertions;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;
import uk.gov.ea.datareturns.App;
import uk.gov.ea.datareturns.aspects.MetricsConstants;
import uk.gov.ea.datareturns.domain.jpa.entities.Parameter;
import uk.gov.ea.datareturns.domain.jpa.entities.UniqueIdentifier;
import uk.gov.ea.datareturns.domain.jpa.entities.Unit;
import uk.gov.ea.datareturns.testsupport.InfluxDBTestImpl;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Tests that validation metrics are extracted correctly.
 */
@RunWith(SpringRunner.class)
@SpringBootTest(classes = App.class, webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
@ActiveProfiles({"IntegrationTests"})
public class ControlledListMetricsAspectTests extends AbstractMetricsTests {

    @Test
    public void testSubstitutionMetrics() {
        uploadFile("testFile.csv");
        List<InfluxDBTestImpl.PointData> data = stub.getPointData().get(MetricsConstants.Common.MEASUREMENT_CONTROLLED_LIST_USAGE);
        Assertions.assertThat(data).isNotNull().isNotEmpty();

        Class<?>[] substitutionEntities = { UniqueIdentifier.class, Parameter.class, Unit.class };
        for (Class<?> c : substitutionEntities) {
            List<InfluxDBTestImpl.PointData> parameterData = data.stream()
                    .filter(pd -> pd.getTags().get(MetricsConstants.ControlledListUsage.TAG_CONTROLLED_LIST).equals(c.getSimpleName()))
                    .collect(Collectors.toList());

            long preferredCount = parameterData.stream()
                    .filter(pd -> "preferred".equals(pd.getTags().get(MetricsConstants.ControlledListUsage.TAG_USAGE_TYPE)))
                    .count();
            long aliasCount = parameterData.stream()
                    .filter(pd -> "alias".equals(pd.getTags().get(MetricsConstants.ControlledListUsage.TAG_USAGE_TYPE)))
                    .count();
            Assertions.assertThat(preferredCount).isEqualTo(4);
            Assertions.assertThat(aliasCount).isEqualTo(20);
        }
    }

}