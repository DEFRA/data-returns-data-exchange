package uk.gov.ea.datareturns.domain.jpa.entities;

import com.fasterxml.jackson.annotation.JsonIgnore;

import javax.persistence.*;

/**
 * The persistent class for the methods_or_standards database table.
 *
 */
@SuppressWarnings({ "JavaDoc", "unused" })
@Entity
@Table(name = "releases_and_transfers")
public class ReleasesAndTransfers implements ControlledListEntity, DependentEntity {

    @Id
    @SequenceGenerator(name = "releases_and_transfers_id_seq", sequenceName = "releases_and_transfers_id_seq", allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "releases_and_transfers_id_seq")
    @JsonIgnore
    private Long id;

    @Basic
    @Column(name = "name", nullable = false, length = 30)
    private String name;

    public Long getId() {
        return this.id;
    }

    public void setId(final Long id) {
        this.id = id;
    }

    public String getName() {
        return this.name;
    }

    public void setName(final String name) {
        this.name = name;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        ReleasesAndTransfers that = (ReleasesAndTransfers) o;

        return name.equals(that.name);
    }

    @Override
    public int hashCode() {
        return name.hashCode();
    }
}