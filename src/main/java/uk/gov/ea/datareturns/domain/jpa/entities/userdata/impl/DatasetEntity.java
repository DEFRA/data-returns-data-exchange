package uk.gov.ea.datareturns.domain.jpa.entities.userdata.impl;

import org.hibernate.annotations.GenericGenerator;
import uk.gov.ea.datareturns.domain.jpa.entities.userdata.Metadata;

import javax.persistence.*;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * @author Graham
 */
@Entity
@Table(name = "ud_datasets")
@GenericGenerator(name = "idGenerator", strategy = "org.hibernate.id.enhanced.SequenceStyleGenerator", parameters = {
        @org.hibernate.annotations.Parameter(name = "sequence_name", value = "ud_datasets_id_seq") }
)
public class DatasetEntity implements Metadata {

    public enum Status {
        UNSUBMITTED,
        SUBMITTED,
        PROCESSING
    }

    @Id @GeneratedValue(generator = "idGenerator")
    private Long id;

    @Basic @Column(name = "identifier", nullable = false, length = 80)
    private String identifier;

    @Basic @Column(name = "originator_email", nullable = false, length = 500)
    private String originatorEmail;

    @ManyToOne(optional = false)
    @JoinColumn(name = "dataset_collection_id")
    private DatasetCollection parentCollection;

    @Enumerated(EnumType.STRING) @Column(name = "status", nullable = false)
    private Status status;

    @OneToMany(mappedBy = "dataset",
            fetch = FetchType.LAZY,
            cascade = CascadeType.REMOVE,
            orphanRemoval = true
    )
    private List<RecordEntity> records = new ArrayList<>();

    @Basic @Column(name = "create_date", nullable = false)
    private Instant createDate;

    @Basic @Column(name = "last_changed_date", nullable = false)
    private Instant lastChangedDate;

    @Basic @Column(name = "record_changed_date", nullable = false)
    private Instant recordChangedDate;

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

    public String getOriginatorEmail() {
        return originatorEmail;
    }

    public void setOriginatorEmail(String originatorEmail) {
        this.originatorEmail = originatorEmail;
    }

    public Status getStatus() {
        return status;
    }

    public void setStatus(Status status) {
        this.status = status;
    }

    public List<RecordEntity> getRecords() {
        return records;
    }

    public void setRecords(List<RecordEntity> records) {
        this.records = records;
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

    public Instant getRecordChangedDate() {
        return recordChangedDate;
    }

    public void setRecordChangedDate(Instant recordChangedDate) {
        this.recordChangedDate = recordChangedDate;
    }

    public DatasetCollection getParentCollection() {
        return parentCollection;
    }

    public void setParentCollection(DatasetCollection parentCollection) {
        this.parentCollection = parentCollection;
    }

    @Override public boolean equals(Object o) {
        if (this == o)
            return true;
        if (!(o instanceof DatasetEntity))
            return false;
        DatasetEntity that = (DatasetEntity) o;
        return Objects.equals(getIdentifier(), that.getIdentifier());
    }

    @Override public int hashCode() {
        return Objects.hash(getIdentifier());
    }
}
