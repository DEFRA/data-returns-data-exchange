package uk.gov.ea.datareturns.domain.jpa.entities.masterdata.impl;

import com.fasterxml.jackson.annotation.JsonIgnore;
import org.hibernate.annotations.GenericGenerator;
import uk.gov.ea.datareturns.domain.jpa.entities.masterdata.ControlledListEntity;
import uk.gov.ea.datareturns.domain.jpa.entities.userdata.impl.DatasetEntity;

import javax.persistence.*;
import java.time.Instant;
import java.util.Collection;
import java.util.Set;

/**
 * @author Graham Willis
 * The persistent class for the unique_identifiers database table.
 *
 */
@Entity
@Table(name = "unique_identifiers")
@GenericGenerator(name = "idGenerator", strategy = "org.hibernate.id.enhanced.SequenceStyleGenerator", parameters = {
        @org.hibernate.annotations.Parameter(name = "sequence_name", value = "unique_identifiers_id_seq") }
)
public class UniqueIdentifier implements ControlledListEntity {

    @Id
    @GeneratedValue(generator = "idGenerator")
    @JsonIgnore
    private Long id;

    @Basic
    @Column(name = "name", nullable = false, length = 10)
    private String name;

    @ManyToOne
    @JoinColumn(name="site_id")
    private Site site;

    @Basic @Column(name = "dataset_changed_date", nullable = false)
    private Instant datasetChangedDate;

    @Basic @Column(name = "create_date", nullable = false)
    private Instant createDate;

    @Basic @Column(name = "last_changed_date", nullable = false)
    private Instant lastChangedDate;

    @ManyToOne
    @JoinColumn(name="unique_identifier_set_id")
    private UniqueIdentifierSet uniqueIdentifierSet;

    @OneToMany(mappedBy="uniqueIdentifier",
            targetEntity=DatasetEntity.class,
            fetch=FetchType.LAZY,
            cascade = CascadeType.REMOVE)
    private Set<DatasetEntity> datasets;

    @OneToMany(mappedBy = "uniqueIdentifier",
            targetEntity = UniqueIdentifierAlias.class,
            fetch = FetchType.LAZY,
            cascade = CascadeType.REMOVE)
    private Set<UniqueIdentifierAlias> uniqueIdentifierAliases;

    public Long getId() {
        return this.id;
    }

    public void setId(final Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Site getSite() {
        return site;
    }

    public void setSite(Site site) {
        this.site = site;
    }

    public Set<DatasetEntity> getDatasets() {
        return datasets;
    }

    public void setDatasets(Set<DatasetEntity> datasets) {
        this.datasets = datasets;
    }

    public UniqueIdentifierSet getUniqueIdentifierSet() {
        return uniqueIdentifierSet;
    }

    public void setUniqueIdentifierSet(UniqueIdentifierSet uniqueIdentifierSet) {
        this.uniqueIdentifierSet = uniqueIdentifierSet;
    }

    public Instant getDatasetChangedDate() {
        return datasetChangedDate;
    }

    public void setDatasetChangedDate(Instant datasetChangedDate) {
        this.datasetChangedDate = datasetChangedDate;
    }

    public Set<UniqueIdentifierAlias> getUniqueIdentifierAliases() {
        return uniqueIdentifierAliases;
    }

    public void setUniqueIdentifierAliases(Set<UniqueIdentifierAlias> uniqueIdentifierAliases) {
        this.uniqueIdentifierAliases = uniqueIdentifierAliases;
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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        UniqueIdentifier that = (UniqueIdentifier) o;

        return name.equals(that.name);
    }

    @Override
    public int hashCode() {
        return name.hashCode();
    }

    @Override
    public String toString() {
        return "UniqueIdentifier{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", site=" + site +
                '}';
    }
}