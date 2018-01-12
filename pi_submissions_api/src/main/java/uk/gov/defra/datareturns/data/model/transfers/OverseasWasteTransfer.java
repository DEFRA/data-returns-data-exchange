package uk.gov.defra.datareturns.data.model.transfers;

import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.GenericGenerator;
import org.hibernate.id.enhanced.SequenceStyleGenerator;
import uk.gov.defra.datareturns.data.model.AbstractBaseEntity;
import uk.gov.defra.datareturns.data.model.Address;
import uk.gov.defra.datareturns.data.model.submissions.Submission;

import javax.persistence.Basic;
import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.ManyToOne;
import java.math.BigDecimal;
import java.util.Objects;

/**
 * PI Overseas waste transfers model
 *
 * @author Sam Gardner-Dell
 */
@Entity(name = "pi_transfer_overseas_waste")
@GenericGenerator(name = AbstractBaseEntity.DEFINITIONS_ID_GENERATOR,
        strategy = AbstractBaseEntity.DEFINITIONS_ID_SEQUENCE_STRATEGY,
        parameters = {
                @org.hibernate.annotations.Parameter(name = SequenceStyleGenerator.SEQUENCE_PARAM, value = "pi_transfer_overseas_waste_id_seq")}
)
@Getter
@Setter
public class OverseasWasteTransfer extends AbstractBaseEntity {
    @ManyToOne
    private Submission submission;

    @Basic
    private String responsibleCompanyName;

    @Embedded
    private Address responsibleCompanyAddress;

    @Embedded
    private Address destinationAddress;

    @Basic
    private int substanceId;

    @Basic
    private BigDecimal tonnage;

    @Enumerated(EnumType.STRING)
    private OverseasWasteTransferOperation operation;

    @Enumerated(EnumType.STRING)
    private OverseasWasteTransferMethod method;

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
        final OverseasWasteTransfer that = (OverseasWasteTransfer) o;
        return Objects.equals(getId(), that.getId());
    }

    @Override
    public final int hashCode() {
        return Objects.hash(getId());
    }

    public enum OverseasWasteTransferOperation {
        Disposal, Recovery;
    }

    public enum OverseasWasteTransferMethod {
        Weighing, Calculation, Estimation;
    }
}
