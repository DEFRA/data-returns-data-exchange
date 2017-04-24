package uk.gov.ea.datareturns.domain.jpa.entities.userdata.impl;

import org.hibernate.annotations.GenericGenerator;
import uk.gov.ea.datareturns.domain.jpa.entities.userdata.Userdata;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Arrays;
import java.util.Date;

/**
 * @author Graham
 */
@Entity
@Table(name = "records")
@GenericGenerator(name = "idGenerator", strategy = "org.hibernate.id.enhanced.SequenceStyleGenerator", parameters = {
        @org.hibernate.annotations.Parameter(name = "sequence_name", value = "records_id_seq") }
)
public class Record implements Serializable, Userdata {

    @Id @GeneratedValue(generator = "idGenerator")
    private Long id;

    @Basic @Column(name = "identifier", nullable = false, length = 80)
    private String identifier;

    @ManyToOne(optional=false)
    @JoinColumn(name = "dataset_id", referencedColumnName = "id")
    private Dataset dataset;

    @ManyToOne(optional=false)
    @JoinColumn(name = "status_id", referencedColumnName = "id")
    private RecordStatus recordStatus;

    // Note: hibernate does not support LocalDatatime yet
    // TODO look into this
    @Basic @Column(name = "create_date", nullable = false)
    private Date createDate;

    @Basic @Column(name = "last_changed_date", nullable = false)
    private Date lastChangedDate;

    @Basic @Column(name = "etag", nullable = false)
    private byte[] etag;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getIdentifier() {
        return identifier;
    }

    public void setIdentifier(String identifier) {
        this.identifier = identifier;
    }

    public Dataset getDataset() {
        return dataset;
    }

    public void setDataset(Dataset dataset) {
        this.dataset = dataset;
    }

    public RecordStatus getRecordStatus() {
        return recordStatus;
    }

    public void setRecordStatus(RecordStatus recordStatus) {
        this.recordStatus = recordStatus;
    }

    public Date getCreateDate() {
        return createDate;
    }

    public void setCreateDate(Date createDate) {
        this.createDate = createDate;
    }

    public Date getLastChangedDate() {
        return lastChangedDate;
    }

    public void setLastChangedDate(Date lastChangedDate) {
        this.lastChangedDate = lastChangedDate;
    }

    public byte[] getEtag() {
        return etag;
    }

    public void setEtag(byte[] etag) {
        this.etag = etag;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Record record = (Record) o;

        if (!identifier.equals(record.identifier)) return false;
        return dataset.equals(record.dataset);

    }

    @Override
    public int hashCode() {
        int result = identifier.hashCode();
        result = 31 * result + dataset.hashCode();
        return result;
    }

    @Override
    public String toString() {
        return "Record{" +
                "id=" + id +
                ", identifier='" + identifier + '\'' +
                ", dataset=" + dataset +
                ", recordStatus=" + recordStatus +
                ", createDate=" + createDate +
                ", lastChangedDate=" + lastChangedDate +
                ", etag=" + Arrays.toString(etag) +
                '}';
    }
}
