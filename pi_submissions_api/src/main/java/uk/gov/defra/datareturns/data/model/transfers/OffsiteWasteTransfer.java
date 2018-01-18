package uk.gov.defra.datareturns.data.model.transfers;

import com.fasterxml.jackson.annotation.JsonBackReference;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.GenericGenerator;
import org.hibernate.id.enhanced.SequenceStyleGenerator;
import uk.gov.defra.datareturns.data.model.AbstractBaseEntity;
import uk.gov.defra.datareturns.data.model.submissions.Submission;

import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.ManyToOne;
import java.math.BigDecimal;
import java.util.Objects;

/**
 * PI Off-site waste transfers model
 *
 * @author Sam Gardner-Dell
 */
@Entity(name = "pi_transfer_offsite")
@GenericGenerator(name = AbstractBaseEntity.DEFINITIONS_ID_GENERATOR,
        strategy = AbstractBaseEntity.DEFINITIONS_ID_SEQUENCE_STRATEGY,
        parameters = {
                @org.hibernate.annotations.Parameter(name = SequenceStyleGenerator.SEQUENCE_PARAM, value = "pi_transfer_offsite_id_seq")}
)
@Getter
@Setter
public class OffsiteWasteTransfer extends AbstractBaseEntity {
    @ManyToOne(optional = false)
    @JsonBackReference
    private Submission submission;

    @Basic
    private int ewcActivityId;

    @Basic
    private int wfdDisposalId;

    @Basic
    private int wfdRecoveryId;

    @Column(nullable = false)
    private BigDecimal tonnage;

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
        final OffsiteWasteTransfer that = (OffsiteWasteTransfer) o;
        return Objects.equals(getId(), that.getId());
    }

    @Override
    public final int hashCode() {
        return Objects.hash(getId());
    }
}
