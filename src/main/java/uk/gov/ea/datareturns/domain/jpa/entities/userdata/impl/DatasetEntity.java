package uk.gov.ea.datareturns.domain.jpa.entities.userdata.impl;

import org.hibernate.annotations.GenericGenerator;
import uk.gov.ea.datareturns.domain.jpa.entities.userdata.Metadata;

import javax.persistence.*;
import java.time.Instant;
import java.util.Collection;

/**
 * @author Graham
 */
@Entity
@Table(name = "datasets")
@GenericGenerator(name = "idGenerator", strategy = "org.hibernate.id.enhanced.SequenceStyleGenerator", parameters = {
        @org.hibernate.annotations.Parameter(name = "sequence_name", value = "datasets_id_seq") }
)
public class DatasetEntity implements Metadata {

    @Id @GeneratedValue(generator = "idGenerator")
    private Long id;

    @Basic @Column(name = "identifier", nullable = false, length = 80)
    private String identifier;

    @Basic @Column(name = "originator_email", nullable = false, length = 500)
    private String originatorEmail;

    @ManyToOne(optional=false)
    @JoinColumn(name = "user_id", referencedColumnName = "id")
    private User user;

    @OneToMany(mappedBy="dataset",targetEntity=RecordEntity.class, fetch=FetchType.LAZY, cascade = CascadeType.REMOVE)
    private Collection records;

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

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public Collection getRecords() {
        return records;
    }

    public void setRecords(Collection records) {
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

    /*
     * The dataset identifier is unique for a given user
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        DatasetEntity dataset = (DatasetEntity) o;

        return identifier.equals(dataset.identifier) && user.equals(dataset.user);
    }

    @Override
    public int hashCode() {
        int result = identifier.hashCode();
        result = 31 * result + user.hashCode();
        return result;
    }

    @Override
    public String toString() {
        return "DatasetEntity{" +
                "id=" + id +
                ", identifier='" + identifier + '\'' +
                ", originatorEmail='" + originatorEmail + '\'' +
                ", user=" + user +
                '}';
    }
}
