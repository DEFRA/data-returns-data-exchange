package uk.gov.ea.datareturns.domain.jpa.entities.userdata.impl;

import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.NaturalId;
import uk.gov.ea.datareturns.domain.jpa.entities.userdata.Metadata;

import javax.persistence.*;
import java.time.Instant;
import java.util.Collection;

/**
 * @author Graham
 */
@Entity
@Table(name = "md_users")
@GenericGenerator(name = "idGenerator", strategy = "org.hibernate.id.enhanced.SequenceStyleGenerator", parameters = {
        @org.hibernate.annotations.Parameter(name = "sequence_name", value = "md_users_id_seq") }
)
public class User implements Metadata {

    public static final String SYSTEM = "SYSTEM";

    @Id @GeneratedValue(generator = "idGenerator")
    private Long id;

    @NaturalId
    @Basic @Column(name = "identifier", nullable = false, length = 80)
    private String identifier;

    @OneToMany(mappedBy="user",targetEntity=DatasetEntity.class, fetch=FetchType.LAZY, cascade = CascadeType.REMOVE)
    private Collection datasets;

    @Basic @Column(name = "create_date", nullable = false)
    private Instant createDate;

    @Basic @Column(name = "last_changed_date", nullable = false)
    private Instant lastChangedDate;

    @Basic @Column(name = "dataset_changed_date", nullable = false)
    private Instant datasetChangedDate;

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

    public Collection getDatasets() {
        return datasets;
    }

    public void setDatasets(Collection datasets) {
        this.datasets = datasets;
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

    public Instant getDatasetChangedDate() {
        return datasetChangedDate;
    }

    public void setDatasetChangedDate(Instant datasetChangedDate) {
        this.datasetChangedDate = datasetChangedDate;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        User user = (User) o;

        return identifier.equals(user.identifier);
    }

    @Override
    public int hashCode() {
        return identifier.hashCode();
    }

    @Override
    public String toString() {
        return "User{" +
                "id=" + id +
                ", identifier='" + identifier + '\'' +
                '}';
    }
}
