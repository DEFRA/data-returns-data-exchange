package uk.gov.ea.datareturns.domain.jpa.entities.userdata.impl;

import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.ImmutableTriple;
import org.apache.commons.lang3.tuple.Pair;
import org.hibernate.annotations.GenericGenerator;
import uk.gov.ea.datareturns.domain.jpa.entities.userdata.AbstractPayloadEntity;
import uk.gov.ea.datareturns.domain.jpa.entities.userdata.Metadata;

import javax.persistence.*;
import java.time.Instant;
import java.util.Set;

/**
 * @author Graham
 */
@Entity
@Table(name = "records")
@GenericGenerator(name = "idGenerator", strategy = "org.hibernate.id.enhanced.SequenceStyleGenerator", parameters = {
        @org.hibernate.annotations.Parameter(name = "sequence_name", value = "records_id_seq") }
)
@SqlResultSetMapping(
        name = "selectValidationErrorsMapping",
        classes = @ConstructorResult(
                targetClass = ImmutableTriple.class,
                columns = {
                        @ColumnResult(name = "identifier", type = String.class),
                        @ColumnResult(name = "payload_type", type = String.class),
                        @ColumnResult(name = "error", type = String.class)
                }))
public class RecordEntity implements Metadata {

     public enum RecordStatus {
        CREATED, PERSISTED, PARSED, INVALID, VALID
    }

    @Id @GeneratedValue(generator = "idGenerator")
    private Long id;

    @OneToOne(cascade = { CascadeType.REMOVE }, mappedBy = "recordEntity")
    private AbstractPayloadEntity abstractPayloadEntity;

    @Basic @Column(name = "identifier", nullable = false, length = 80)
    private String identifier;

    @Basic @Column(name = "payload_type", length = 100)
    private String payloadType;

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

    @ManyToMany
    @JoinTable(name = "record_validation_errors", joinColumns =
            @JoinColumn(name = "record_id", referencedColumnName = "id"), inverseJoinColumns = {
            @JoinColumn(name = "payload_type", referencedColumnName = "payload_type"),
            @JoinColumn(name = "error", referencedColumnName = "error")
        }
    )
    public Set<ValidationError> validationErrors;

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

    public void setPayloadType(String payloadType) {
        this.payloadType = payloadType;
    }

    public String getPayloadType() {
        return payloadType;
    }

    public String getJson() {
        return json;
    }

    public void setJson(String json) {
        this.json = json;
    }

    public AbstractPayloadEntity getAbstractPayloadEntity() {
        return abstractPayloadEntity;
    }

    public void setAbstractPayloadEntity(AbstractPayloadEntity abstractPayloadEntity) {
        this.abstractPayloadEntity = abstractPayloadEntity;
    }

    public Set<ValidationError> getValidationErrors() {
        return validationErrors;
    }

    public void setValidationErrors(Set<ValidationError> validationErrors) {
        this.validationErrors = validationErrors;
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
                ", abstractPayloadEntity=" + abstractPayloadEntity +
                ", identifier='" + identifier + '\'' +
                ", dataset=" + dataset +
                '}';
    }
}