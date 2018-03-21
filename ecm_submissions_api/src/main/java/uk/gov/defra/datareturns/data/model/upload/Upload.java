package uk.gov.defra.datareturns.data.model.upload;

import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.GenericGenerator;
import org.hibernate.id.enhanced.SequenceStyleGenerator;
import uk.gov.defra.datareturns.data.model.AbstractBaseEntity;
import uk.gov.defra.datareturns.data.model.dataset.Dataset;
import uk.gov.defra.datareturns.data.model.record.Record;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.OneToMany;
import java.util.List;
import java.util.Objects;

/**
 * ECM Submission Record
 *
 * @author Sam Gardner-Dell
 */
@Entity(name = "ecm_upload")
@GenericGenerator(name = AbstractBaseEntity.DEFINITIONS_ID_GENERATOR,
                  strategy = AbstractBaseEntity.DEFINITIONS_ID_SEQUENCE_STRATEGY,
                  parameters = {
                          @org.hibernate.annotations.Parameter(name = SequenceStyleGenerator.SEQUENCE_PARAM, value = "ecm_upload_id_seq")
                  }
)
@Getter
@Setter
public class Upload extends AbstractBaseEntity {
    private String filename;

    @OneToMany(mappedBy = "upload",
               fetch = FetchType.LAZY,
               cascade = CascadeType.REMOVE,
               orphanRemoval = true
    )
    private List<Dataset> datasets;

    // FIXME: Don't use equality based on primary key!
    @Override
    public boolean equals(final Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Record)) {
            return false;
        }
        if (getId() == null) {
            return false;
        }
        final Record record = (Record) o;
        return Objects.equals(getId(), record.getId());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getId());
    }
}
