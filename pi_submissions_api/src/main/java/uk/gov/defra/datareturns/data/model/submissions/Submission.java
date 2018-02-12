package uk.gov.defra.datareturns.data.model.submissions;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.GenericGenerator;
import org.hibernate.envers.Audited;
import org.hibernate.id.enhanced.SequenceStyleGenerator;
import uk.gov.defra.datareturns.data.model.AbstractBaseEntity;
import uk.gov.defra.datareturns.data.model.releases.ReleaseToAir;
import uk.gov.defra.datareturns.data.model.releases.ReleaseToControlledWater;
import uk.gov.defra.datareturns.data.model.releases.ReleaseToLand;
import uk.gov.defra.datareturns.data.model.releases.ReleaseToWasteWater;
import uk.gov.defra.datareturns.data.model.transfers.OffsiteWasteTransfer;
import uk.gov.defra.datareturns.data.model.transfers.OverseasWasteTransfer;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;
import javax.validation.Valid;
import java.util.Objects;
import java.util.Set;

/**
 * PI Submission
 *
 * @author Sam Gardner-Dell
 */
@Entity(name = "pi_submission")
@Table(uniqueConstraints = {
        @UniqueConstraint(name = "uniq_reference_and_year", columnNames = {"reportingReference", "applicableYear"})
})
@GenericGenerator(name = AbstractBaseEntity.DEFINITIONS_ID_GENERATOR,
        strategy = AbstractBaseEntity.DEFINITIONS_ID_SEQUENCE_STRATEGY,
        parameters = {
                @org.hibernate.annotations.Parameter(name = SequenceStyleGenerator.SEQUENCE_PARAM, value = "pi_submission_id_seq")}
)
@Audited
@Getter
@Setter
public class Submission extends AbstractBaseEntity {
    /**
     * The unique identifier for the operation the submission relates to (permit or reference number)
     */
    @Column(nullable = false)
    private Long reportingReference;

    /**
     * The year that the submission is applicable to (not necessarily the year of submission)
     */
    @Column(nullable = false)
    private short applicableYear;

    @Enumerated(EnumType.STRING)
    private SubmissionStatus status;

    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true, mappedBy = "submission")
    @JsonManagedReference
    @Valid
    private Set<ReleaseToAir> releasesToAir;

    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true, mappedBy = "submission")
    @JsonManagedReference
    @Valid
    private Set<ReleaseToLand> releasesToLand;

    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true, mappedBy = "submission")
    @JsonManagedReference
    @Valid
    private Set<ReleaseToControlledWater> releasesToControlledWater;

    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true, mappedBy = "submission")
    @JsonManagedReference
    @Valid
    private Set<ReleaseToWasteWater> releasesToWasteWater;

    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true, mappedBy = "submission")
    @JsonManagedReference
    @Valid
    private Set<OverseasWasteTransfer> overseasWasteTransfers;

    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true, mappedBy = "submission")
    @JsonManagedReference
    @Valid
    private Set<OffsiteWasteTransfer> offsiteWasteTransfers;

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
        final Submission that = (Submission) o;
        return Objects.equals(getId(), that.getId());
    }

    @Override
    public final int hashCode() {
        return Objects.hash(getId());
    }


    public enum SubmissionStatus {
        Unsubmitted,
        Submitted,
        Approved;
    }
}
