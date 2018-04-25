package uk.gov.defra.datareturns.data.model.transfers;

import com.fasterxml.jackson.annotation.JsonBackReference;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.GenericGenerator;
import org.hibernate.envers.Audited;
import org.hibernate.id.enhanced.SequenceStyleGenerator;
import uk.gov.defra.datareturns.data.model.AbstractBaseEntity;
import uk.gov.defra.datareturns.data.model.Address;
import uk.gov.defra.datareturns.validation.validators.transfers.ValidOverseasTransfer;

import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.ManyToOne;
import javax.validation.Valid;
import java.math.BigDecimal;

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
@ValidOverseasTransfer
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
}
