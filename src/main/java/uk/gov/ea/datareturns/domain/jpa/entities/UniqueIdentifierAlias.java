package uk.gov.ea.datareturns.domain.jpa.entities;

import javax.persistence.*;

/**
 * @author Graham Willis
 * The persistent class for the unique_identifiers database table.
 *
 */
@SuppressWarnings({ "JavaDoc", "unused" })
@Entity
@Table(name = "unique_identifier_aliases")
public class UniqueIdentifierAlias implements ControlledListEntity {

    @Id
    @SequenceGenerator(name = "unique_identifier_aliases_id_seq", sequenceName = "unique_identifier_aliases_id_seq", allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "unique_identifier_aliases_id_seq")
    private Long id;

    @Basic
    @Column(name = "name", nullable = false, length = 10)
    private String name;

    @ManyToOne
    @JoinColumn(name="unique_id")
    private UniqueIdentifier uniqueIdentifier;

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

    public UniqueIdentifier getUniqueIdentifier() {
        return uniqueIdentifier;
    }

    public void setUniqueIdentifier(UniqueIdentifier uniqueIdentifier) {
        this.uniqueIdentifier = uniqueIdentifier;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        UniqueIdentifierAlias that = (UniqueIdentifierAlias) o;

        return name.equals(that.name);
    }

    @Override
    public int hashCode() {
        return name.hashCode();
    }

    @Override
    public String toString() {
        return "UniqueIdentifierAlias{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", uniqueIdentifier=" + uniqueIdentifier +
                '}';
    }
}