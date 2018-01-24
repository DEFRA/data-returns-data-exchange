package uk.gov.defra.datareturns.data.model.releases;

import com.fasterxml.jackson.annotation.JsonBackReference;
import lombok.Getter;
import lombok.Setter;
import uk.gov.defra.datareturns.data.model.AbstractBaseEntity;
import uk.gov.defra.datareturns.data.model.submissions.Submission;
import uk.gov.defra.datareturns.validation.validators.id.ValidId;

import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.ManyToOne;
import javax.persistence.MappedSuperclass;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Objects;

/**
 * The mapped superclass for releases entities
 *
 * @author Sam Gardner-Dell
 */
@MappedSuperclass
@Getter
@Setter
public abstract class AbstractReleaseEntity extends AbstractBaseEntity implements Serializable {
    @ManyToOne(optional = false)
    @JsonBackReference
    private Submission submission;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private ReleaseMethod method;

    // Fixme - need class level validation for BRT = false
    @Column
    private BigDecimal value;

    @Basic
    @Column(nullable = false)
    private boolean belowReportingThreshold;

    // Fixme - need class level validation for BRT = false
    // @ValidId(resourceCollectionUris = "unitGroups/2/units")
    @Column
    private int unitId;

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

