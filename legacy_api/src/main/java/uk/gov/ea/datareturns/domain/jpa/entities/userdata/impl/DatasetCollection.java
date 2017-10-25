package uk.gov.ea.datareturns.domain.jpa.entities.userdata.impl;

import org.hibernate.annotations.GenericGenerator;
import uk.gov.ea.datareturns.domain.jpa.entities.masterdata.impl.UniqueIdentifier;

import javax.persistence.*;
import java.io.Serializable;
import java.time.Instant;
import java.util.List;

/**
 * Associate a collection of datasets to a given unique identifier.
 *
 * @author Sam Gardner-Dell
 */
@Entity
@Table(name = "ud_dataset_collections")
@GenericGenerator(name = "idGenerator", strategy = "org.hibernate.id.enhanced.SequenceStyleGenerator", parameters = {
        @org.hibernate.annotations.Parameter(name = "sequence_name", value = "ud_dataset_collections_id_seq") }
)
public class DatasetCollection implements Serializable {
    @Id @GeneratedValue(generator = "idGenerator")
    private Long id;

    @OneToOne
    @JoinColumn(name = "unique_identifier_id")
    private UniqueIdentifier uniqueIdentifier;

    @Basic @Column(name = "create_date", nullable = false)
    private Instant createDate;

    @Basic @Column(name = "last_changed_date", nullable = false)
    private Instant lastChangedDate;

    @OneToMany(
            mappedBy = "parentCollection",
            cascade = CascadeType.REMOVE,
            orphanRemoval = true)
    private List<DatasetEntity> datasets;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public UniqueIdentifier getUniqueIdentifier() {
        return uniqueIdentifier;
    }

    public void setUniqueIdentifier(UniqueIdentifier uniqueIdentifier) {
        this.uniqueIdentifier = uniqueIdentifier;
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

    public List<DatasetEntity> getDatasets() {
        return datasets;
    }

    public void setDatasets(List<DatasetEntity> datasets) {
        this.datasets = datasets;
    }
}
