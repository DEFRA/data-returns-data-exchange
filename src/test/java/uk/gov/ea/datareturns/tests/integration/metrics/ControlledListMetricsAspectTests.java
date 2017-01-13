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
@ActiveProfiles({ "IntegrationTests" })
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

            // Preferred usage count should be 4 for each list type
            long preferredCount = parameterData.stream()
                    .filter(pd -> "preferred".equals(pd.getTags().get(MetricsConstants.ControlledListUsage.TAG_USAGE_TYPE)))
                    .mapToLong(pd -> (Long) pd.getFields().get(MetricsConstants.ControlledListUsage.FIELD_USAGE_COUNT))
                    .sum();
            Assertions.assertThat(preferredCount).isEqualTo(4);

            // Alias count should be 20 for each list type
            long aliasCount = parameterData.stream()
                    .filter(pd -> "alias".equals(pd.getTags().get(MetricsConstants.ControlledListUsage.TAG_USAGE_TYPE)))
                    .mapToLong(pd -> (Long) pd.getFields().get(MetricsConstants.ControlledListUsage.FIELD_USAGE_COUNT))
                    .sum();
            Assertions.assertThat(aliasCount).isEqualTo(20);

            // Although there are numerous EA_ID's in the test file, they should all map to the preferred value for metrics use.
            long uniqueEaIdCount = parameterData.stream()
                    .map(pd -> pd.getTags().get(MetricsConstants.ControlledListUsage.TAG_EA_ID))
                    .distinct().count();
            Assertions.assertThat(uniqueEaIdCount).isEqualTo(1);
        }
    }
}