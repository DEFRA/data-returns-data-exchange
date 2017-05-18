package uk.gov.ea.datareturns.domain.jpa.entities.userdata.impl;

import org.hibernate.annotations.GenericGenerator;
import uk.gov.ea.datareturns.domain.jpa.entities.userdata.AbstractObservation;
import uk.gov.ea.datareturns.domain.jpa.entities.userdata.Metadata;

import javax.persistence.*;
import java.time.Instant;

/**
 * @author Graham
 */
@Entity
@Table(name = "records")
@GenericGenerator(name = "idGenerator", strategy = "org.hibernate.id.enhanced.SequenceStyleGenerator", parameters = {
        @org.hibernate.annotations.Parameter(name = "sequence_name", value = "records_id_seq") }
)
public class RecordEntity implements Metadata {

    public enum RecordStatus {
        CREATED, PERSISTED, PARSED, INVALID, VALID, SUBMITTED
    }

    @Id @GeneratedValue(generator = "idGenerator")
    private Long id;

    @OneToOne(cascade = { CascadeType.REMOVE }, mappedBy = "recordEntity")
    private AbstractObservation abstractObservation;

    @Basic @Column(name = "identifier", nullable = false, length = 80)
    private String identifier;

    @ManyToOne(optional=false)
    @JoinColumn(name = "dataset_id", referencedColumnName = "id")
    private DatasetEntity dataset;

    @Enumerated(EnumType.STRING)
    @Column(name = "status")
    private RecordStatus recordStatus;

    @Basic @Column(name = "create_date", nullable = false)
    private Instant createDate;

    @Basic @Column(name = "last_changed_date", nullable = false)
    private Instant lastChangedDate;

    @Basic @Column(name = "json", length = 16000)
    private String json;

    @Basic @Column(name = "validation_result", length = 16000)
    private String validationResult;

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

    public DatasetEntity getDataset() {
        return dataset;
    }

    public void setDataset(DatasetEntity dataset) {
        this.dataset = dataset;
    }

    public RecordStatus getRecordStatus() {
        return recordStatus;
    }

    public void setRecordStatus(RecordStatus recordStatus) {
        this.recordStatus = recordStatus;
    }

    public Instant getCreateDate() {
        return createDate;
    }

    public void setCreateDate(Instant createDate) {
        this.createDate = createDate;
    }

    public Instant getLastChangedDate() {
        return lastChangedDate;
    }

    public void setLastChangedDate(Instant lastChangedDate) {
        this.lastChangedDate = lastChangedDate;
    }

    public String getJson() {
        return json;
    }

    public void setJson(String json) {
        this.json = json;
    }

    public String getValidationResult() {
        return validationResult;
    }

    public void setValidationResult(String validationResult) {
        this.validationResult = validationResult;
    }

    public AbstractObservation getAbstractObservation() {
        return abstractObservation;
    }

    public void setAbstractObservation(AbstractObservation abstractObservation) {
        this.abstractObservation = abstractObservation;
    }


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        RecordEntity recordEntity = (RecordEntity) o;

        return identifier.equals(recordEntity.identifier) && dataset.equals(recordEntity.dataset);
    }

    @Override
    public int hashCode() {
        int result = identifier.hashCode();
        result = 31 * result + dataset.hashCode();
        return result;
    }

    @Override
    public String toString() {
        return "RecordEntity{" +
                "id=" + id +
                ", abstractObservation=" + abstractObservation +
                ", identifier='" + identifier + '\'' +
                ", dataset=" + dataset +
                ", recordStatus=" + recordStatus +
                ", createDate=" + createDate +
                ", lastChangedDate=" + lastChangedDate +
                ", json='" + json + '\'' +
                ", validationResult=" + validationResult +
                '}';
    }
}