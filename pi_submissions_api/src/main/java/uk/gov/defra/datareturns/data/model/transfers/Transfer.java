package uk.gov.defra.datareturns.data.model.transfers;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.GenericGenerator;
import org.hibernate.envers.Audited;
import org.hibernate.id.enhanced.SequenceStyleGenerator;
import org.springframework.data.rest.core.annotation.RestResource;
import uk.gov.defra.datareturns.data.model.AbstractBaseEntity;
import uk.gov.defra.datareturns.data.model.submissions.Submission;
import uk.gov.defra.datareturns.validation.validators.transfers.ValidTransfer;

import javax.persistence.Basic;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.validation.Valid;
import java.math.BigDecimal;
import java.util.Set;

/**
 * PI Off-site waste transfers model
 *
 * @author Sam Gardner-Dell
 */
@Entity(name = "pi_transfer")
@GenericGenerator(name = AbstractBaseEntity.DEFINITIONS_ID_GENERATOR,
                  strategy = AbstractBaseEntity.DEFINITIONS_ID_SEQUENCE_STRATEGY,
                  parameters = {
                          @org.hibernate.annotations.Parameter(name = SequenceStyleGenerator.SEQUENCE_PARAM, value = "pi_transfer_id_seq")
                  }
)
@Audited
@ValidTransfer
@Getter
@Setter
public class Transfer extends AbstractBaseEntity {
    @ManyToOne(optional = false)
    @JsonBackReference
    private Submission submission;

    @Basic
    private Integer ewcActivityId;

    @Basic
    private Integer wfdDisposalId;

    @Basic
    private Integer wfdRecoveryId;

    @Column(precision = 30, scale = 15)
    private BigDecimal tonnage;

    @Basic
    @Column(nullable = false)
    private boolean belowReportingThreshold;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private TransferMethod method;

    @RestResource(path = "overseasTransfers", rel = "overseasTransfers")
    @JsonProperty(value = "overseas_transfers")
    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true, mappedBy = "transfer")
    @JsonManagedReference
    @Valid
    private Set<OverseasTransfer> overseas;
}
