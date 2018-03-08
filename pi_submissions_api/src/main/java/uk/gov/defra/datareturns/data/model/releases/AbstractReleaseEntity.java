package uk.gov.defra.datareturns.data.model.releases;

import com.fasterxml.jackson.annotation.JsonBackReference;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.envers.Audited;
import uk.gov.defra.datareturns.data.model.AbstractBaseEntity;
import uk.gov.defra.datareturns.data.model.submissions.Submission;
import uk.gov.defra.datareturns.service.ValueStandardisationService;
import uk.gov.defra.datareturns.util.SpringApplicationContextProvider;

import javax.persistence.*;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Objects;

/**
 * The mapped superclass for releases entities
 *
 * @author Sam Gardner-Dell
 */
@MappedSuperclass
@Audited
@Getter
@Setter
public abstract class AbstractReleaseEntity extends AbstractBaseEntity implements Serializable {
    @ManyToOne(optional = false)
    @JsonBackReference
    private Submission submission;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private ReleaseMethod method;

    @Column(precision = 30, scale = 15)
    private BigDecimal value;

    @Column(precision = 30, scale = 15)
    private BigDecimal standardValue;

    @Basic
    @Column(nullable = false)
    private boolean belowReportingThreshold;

    @Column
    private int unitId;

    @PrePersist
    private void calculateStandardValue() {
        final ValueStandardisationService svc = SpringApplicationContextProvider.getApplicationContext().getBean(ValueStandardisationService.class);
        this.standardValue = svc.getStandardValue(value, String.valueOf(unitId));
    }

    @Column
    private BigDecimal notifiableValue;

    @Column
    private int notifiableUnitId;

    @Column(length = 500)
    private String notifiableReason;

    @Override
    public final boolean equals(final Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        if (getId() == null) {
            return false;
        }
        final AbstractReleaseEntity that = (AbstractReleaseEntity) o;
        return Objects.equals(getId(), that.getId());
    }

    @Override
    public final int hashCode() {
        return Objects.hash(getId());
    }

    public enum ReleaseMethod {
        Measurement,
        Calculation,
        Estimation;
    }
}

