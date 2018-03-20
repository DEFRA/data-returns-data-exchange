package uk.gov.defra.datareturns.data.model.releases;

import com.fasterxml.jackson.annotation.JsonBackReference;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.GenericGenerator;
import org.hibernate.envers.Audited;
import org.hibernate.id.enhanced.SequenceStyleGenerator;
import uk.gov.defra.datareturns.data.model.AbstractBaseEntity;
import uk.gov.defra.datareturns.data.model.submissions.Submission;
import uk.gov.defra.datareturns.service.ValueStandardisationService;
import uk.gov.defra.datareturns.util.SpringApplicationContextProvider;
import uk.gov.defra.datareturns.validation.validators.releases.ValidRelease;

import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.ManyToOne;
import javax.persistence.PrePersist;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;
import java.math.BigDecimal;
import java.util.Objects;

/**
 * PI Releases model
 *
 * @author Sam Gardner-Dell
 */
@Entity(name = "pi_release")
@Table(
        uniqueConstraints = {
                @UniqueConstraint(name = "uniq_submission_route_substance", columnNames = {"submission_id", "routeId", "substanceId"})
        }
)
@GenericGenerator(name = AbstractBaseEntity.DEFINITIONS_ID_GENERATOR,
        strategy = AbstractBaseEntity.DEFINITIONS_ID_SEQUENCE_STRATEGY,
        parameters = {
                @org.hibernate.annotations.Parameter(name = SequenceStyleGenerator.SEQUENCE_PARAM, value = "pi_release_id_seq")}
)
@Audited
@ValidRelease
@Getter
@Setter
public final class Release extends AbstractBaseEntity {
    @ManyToOne(optional = false)
    @JsonBackReference
    private Submission submission;

    @Column(nullable = false)
    private int substanceId;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private ReleaseMethod method;

    @Column(precision = 30, scale = 15)
    private BigDecimal value;

    @Column(nullable = false)
    private int routeId;

    @Column(nullable = true)
    private int subrouteId;

    @Column(precision = 30, scale = 15)
    private BigDecimal standardValue;

    @Basic
    @Column(nullable = false)
    private boolean belowReportingThreshold;

    @Column
    private int unitId;

    @Column(precision = 30, scale = 15)
    private BigDecimal notifiableValue;

    @Column
    private int notifiableUnitId;

    @Column(length = 500)
    private String notifiableReason;

    @PrePersist
    private void calculateStandardValue() {
        final ValueStandardisationService svc = SpringApplicationContextProvider.getApplicationContext().getBean(ValueStandardisationService.class);
        this.standardValue = svc.getStandardValue(value, String.valueOf(unitId));
    }

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
        final Release that = (Release) o;
        return Objects.equals(getId(), that.getId());
    }

    @Override
    public final int hashCode() {
        return Objects.hash(getId());
    }

}

