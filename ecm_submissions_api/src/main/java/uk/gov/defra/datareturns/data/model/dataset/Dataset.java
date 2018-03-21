package uk.gov.defra.datareturns.data.model.dataset;

import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.GenericGenerator;
import org.hibernate.id.enhanced.SequenceStyleGenerator;
import org.hibernate.validator.constraints.NotBlank;
import uk.gov.defra.datareturns.data.model.AbstractBaseEntity;
import uk.gov.defra.datareturns.data.model.record.Record;
import uk.gov.defra.datareturns.data.model.upload.Upload;
import uk.gov.defra.datareturns.service.MasterDataEntity;
import uk.gov.defra.datareturns.validation.constraints.controlledlist.ControlledList;

import javax.persistence.Basic;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.validation.Valid;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

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
//    @Basic
//    @Column(name = "identifier", nullable = false, length = 80)
//    private String identifier;

    @Basic
    @Column(name = "originator_email", length = 500)
    private String originatorEmail;

    @Basic
    @Column(name = "ea_id", nullable = false)
    @NotBlank(message = "DR9000-Missing")
    @ControlledList(entities = {MasterDataEntity.EA_ID, MasterDataEntity.EA_ID_ALIAS}, message = "DR9000-Incorrect")
    private String eaId;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private Status status = Status.UNSUBMITTED;

    @OneToMany(mappedBy = "dataset",
               fetch = FetchType.LAZY,
               cascade = CascadeType.REMOVE,
               orphanRemoval = true
    )
    @Valid
    private List<Record> records = new ArrayList<>();

    @ManyToOne
    @JoinColumn(name = "upload", updatable = false)
    private Upload upload;

    // FIXME: Don't use equality based on primary key!
    @Override
    public boolean equals(final Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Dataset)) {
            return false;
        }
        if (getId() == null) {
            return false;
        }
        final Dataset that = (Dataset) o;
        return Objects.equals(getId(), that.getId());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getId());
    }

    public enum Status {
        UNSUBMITTED,
        SUBMITTED
    }
}
