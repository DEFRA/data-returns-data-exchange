package uk.gov.defra.datareturns.data.model.threshold;


import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.GenericGenerator;
import org.hibernate.id.enhanced.SequenceStyleGenerator;
import uk.gov.defra.datareturns.data.model.AbstractBaseEntity;
import uk.gov.defra.datareturns.data.model.parameter.Parameter;
import uk.gov.defra.datareturns.data.model.regimeobligation.RegimeObligation;
import uk.gov.defra.datareturns.data.model.unit.Unit;

import javax.persistence.Cacheable;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.ManyToOne;
import java.io.Serializable;
import java.math.BigDecimal;

/**
 * The persistent class for the md_threshold database table.
 *
 * @author Sam Gardner-Dell
 */
@Entity(name = "md_threshold")
@Cacheable
@GenericGenerator(name = AbstractBaseEntity.DEFINITIONS_ID_GENERATOR,
                  strategy = AbstractBaseEntity.DEFINITIONS_ID_SEQUENCE_STRATEGY,
                  parameters = {
                          @org.hibernate.annotations.Parameter(name = SequenceStyleGenerator.SEQUENCE_PARAM, value = "md_threshold_id_seq")
                  }
)
@Getter
@Setter
public class Threshold extends AbstractBaseEntity implements Serializable {
    @ManyToOne(optional = false)
    private RegimeObligation regimeObligation;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private ThresholdType type;

    @ManyToOne(optional = false)
    private Parameter parameter;

    @Column(precision = 30, scale = 15, nullable = false)
    private BigDecimal value;

    @ManyToOne(optional = false)
    private Unit unit;

    @RequiredArgsConstructor
    @Getter
    public enum ThresholdType {
        /**
         * The threshold at which a specific numeric value must be reported
         */
        REPORTING_THRESHOLD("Minimum value at which a specific numeric value must be reported");

        private final String description;
    }
}
