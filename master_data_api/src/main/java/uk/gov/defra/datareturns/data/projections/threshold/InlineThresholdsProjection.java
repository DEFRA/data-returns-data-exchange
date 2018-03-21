package uk.gov.defra.datareturns.data.projections.threshold;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.rest.core.config.Projection;
import uk.gov.defra.datareturns.data.model.threshold.Threshold;

import java.math.BigDecimal;

/**
 * InlineThresholdsProjection to display all threshold data inline
 *
 * @author Sam Gardner-Dell
 */
@Projection(name = "inlineThresholds", types = Threshold.class)
@SuppressWarnings("unused")
public interface InlineThresholdsProjection {
    @Value("#{target.parameter.nomenclature}")
    String getParameter();

    @Value("#{target.unit.nomenclature}")
    String getUnit();

    Threshold.ThresholdType getType();

    BigDecimal getValue();
}
