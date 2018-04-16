package uk.gov.defra.datareturns.data.model.transfers;

import com.fasterxml.jackson.annotation.JsonBackReference;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.GenericGenerator;
import org.hibernate.envers.Audited;
import org.hibernate.id.enhanced.SequenceStyleGenerator;
import uk.gov.defra.datareturns.data.model.AbstractBaseEntity;
import uk.gov.defra.datareturns.data.model.Address;

import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.ManyToOne;
import javax.validation.Valid;
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
                          @org.hibernate.annotations.Parameter(name = SequenceStyleGenerator.SEQUENCE_PARAM,
                                                               value = "pi_transfer_overseas_waste_id_seq")
                  }
)
@Audited
@Getter
@Setter
public class OverseasTransfer extends AbstractBaseEntity {
    @ManyToOne(optional = false)
    @JsonBackReference
    private Transfer transfer;

    @Basic
    private String responsibleCompanyName;

    @Embedded
    @Valid
    private Address responsibleCompanyAddress;

    @Embedded
    @Valid
    private Address destinationAddress;

    @Column(nullable = false, precision = 30, scale = 15)
    private BigDecimal tonnage;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private TransferMethod method;

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
        final OverseasTransfer that = (OverseasTransfer) o;
        return Objects.equals(getId(), that.getId());
    }

    @Override
    public final int hashCode() {
        return Objects.hash(getId());
    }

}
