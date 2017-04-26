package uk.gov.ea.datareturns.domain.jpa.entities.userdata;

import uk.gov.ea.datareturns.domain.jpa.entities.userdata.impl.DataSampleSubmission;
import uk.gov.ea.datareturns.domain.jpa.entities.userdata.impl.Record;

import javax.persistence.*;
import java.io.Serializable;

/**
 * @author Graham Willis
 */
@Entity
@Inheritance(strategy = InheritanceType.TABLE_PER_CLASS)
public abstract class Submission  implements Serializable {

    @Id
    @OneToOne(optional = false)
    @JoinColumn(name = "record_id", referencedColumnName = "id")
    private Record record;

    public Record getRecord() {
        return record;
    }
    public void setRecord(Record record) {
        this.record = record;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Submission that = (Submission) o;

        return record.equals(that.record);
    }

    @Override
    public int hashCode() {
        return record.hashCode();
    }

}
