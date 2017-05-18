package uk.gov.ea.datareturns.domain.jpa.entities.userdata;

import uk.gov.ea.datareturns.domain.jpa.entities.userdata.impl.RecordEntity;

import javax.persistence.*;

/**
 * @author Graham Willis
 */
@Entity
@Inheritance(strategy = InheritanceType.TABLE_PER_CLASS)
public abstract class AbstractObservation implements Userdata {

    @Id
    @OneToOne(optional = false)
    @JoinColumn(name = "record_id", referencedColumnName = "id")
    private RecordEntity recordEntity;

    public RecordEntity getRecordEntity() {
        return recordEntity;
    }
    public void setRecordEntity(RecordEntity recordEntity) {
        this.recordEntity = recordEntity;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        AbstractObservation that = (AbstractObservation) o;

        return recordEntity.equals(that.recordEntity);
    }

    @Override
    public int hashCode() {
        return recordEntity.hashCode();
    }
}
