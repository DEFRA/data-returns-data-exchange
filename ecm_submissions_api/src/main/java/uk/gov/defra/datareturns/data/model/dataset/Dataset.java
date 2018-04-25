package uk.gov.defra.datareturns.data.model.dataset;

import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.GenericGenerator;
import org.hibernate.id.enhanced.SequenceStyleGenerator;
import uk.gov.defra.datareturns.data.model.AbstractBaseEntity;
import uk.gov.defra.datareturns.data.model.record.Record;
import uk.gov.defra.datareturns.data.model.upload.Upload;
import uk.gov.defra.datareturns.service.csv.EcmErrorCodes;
import uk.gov.defra.datareturns.validation.service.MasterDataEntity;
import uk.gov.defra.datareturns.validation.validators.id.ValidId;

import javax.persistence.Basic;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import java.util.LinkedHashSet;
import java.util.Set;

/**
 * ECM Submission Dataset
 *
 * @author Sam Gardner-Dell
 */
@Entity(name = "ecm_dataset")
@GenericGenerator(name = AbstractBaseEntity.DEFINITIONS_ID_GENERATOR,
                  strategy = AbstractBaseEntity.DEFINITIONS_ID_SEQUENCE_STRATEGY,
                  parameters = {
                          @org.hibernate.annotations.Parameter(name = SequenceStyleGenerator.SEQUENCE_PARAM, value = "ecm_dataset_id_seq")
                  }
)
@Getter
@Setter
public class Dataset extends AbstractBaseEntity {
    @Basic
    @Column(name = "originator_email", length = 500)
    private String originatorEmail;

    @Basic
    @Column(name = "ea_id", nullable = false)
    @NotNull(message = EcmErrorCodes.Missing.EA_ID)
    // FIXME: Validate EA_ID against the ECM regime
    @ValidId(entity = MasterDataEntity.UNIQUE_IDENTIFIER, message = EcmErrorCodes.Incorrect.EA_ID)
    private Long eaId;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private Status status = Status.UNSUBMITTED;

    @OneToMany(mappedBy = "dataset", cascade = CascadeType.ALL, orphanRemoval = true)
    @Valid
    private Set<Record> records = new LinkedHashSet<>();

    @ManyToOne(optional = true)
    private Upload upload;

    /**
     * Add a new record to the dataset
     *
     * @param record the {@link Record} to be added
     * @return true if the Record was added, false otherwise
     */
    public boolean addRecord(final Record record) {
        return this.records.add(record);
    }

    public enum Status {
        UNSUBMITTED,
        SUBMITTED
    }
}
