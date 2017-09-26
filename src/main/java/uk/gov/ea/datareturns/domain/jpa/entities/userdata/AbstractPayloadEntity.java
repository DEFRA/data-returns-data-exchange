package uk.gov.ea.datareturns.domain.jpa.entities.userdata;

import uk.gov.ea.datareturns.domain.jpa.entities.userdata.impl.DatasetEntity;
import uk.gov.ea.datareturns.domain.jpa.entities.userdata.impl.RecordEntity;

import javax.persistence.*;
import java.util.Objects;

/**
 * @author Graham Willis
 */
@Entity
@Inheritance(strategy = InheritanceType.TABLE_PER_CLASS)
@NamedQueries({
        @NamedQuery(
                name = "AbstractPayloadEntity.forDataset",
                query = "select p from AbstractPayloadEntity as p where p.dataset = :dataset"
        )
})
public abstract class AbstractPayloadEntity implements Userdata {
    @Id
    @OneToOne(optional = false, cascade = { CascadeType.REMOVE })
    @JoinColumn(name = "record_id", referencedColumnName = "id")
    private RecordEntity recordEntity;

    @OneToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "dataset_id", referencedColumnName = "id")
    private DatasetEntity dataset;

    public DatasetEntity getDataset() {
        return dataset;
    }

    public void setDataset(DatasetEntity dataset) {
        this.dataset = dataset;
    }

    public RecordEntity getRecordEntity() {
        return recordEntity;
    }

    public void setRecordEntity(RecordEntity recordEntity) {
        this.recordEntity = recordEntity;
    }

    @Override public boolean equals(Object o) {
        if (this == o)
            return true;
        if (o == null || getClass() != o.getClass())
            return false;
        AbstractPayloadEntity that = (AbstractPayloadEntity) o;
        return Objects.equals(recordEntity, that.recordEntity) &&
                Objects.equals(dataset, that.dataset);
    }

    @Override public int hashCode() {
        return Objects.hash(recordEntity, dataset);
    }
}
