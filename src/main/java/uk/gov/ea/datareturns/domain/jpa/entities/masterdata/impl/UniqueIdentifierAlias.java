package uk.gov.ea.datareturns.domain.jpa.entities.masterdata.impl;

import com.fasterxml.jackson.annotation.JsonIgnore;
import org.hibernate.annotations.GenericGenerator;
import uk.gov.ea.datareturns.domain.jpa.entities.masterdata.ControlledListEntity;
import uk.gov.ea.datareturns.domain.model.fields.FieldValue;

import javax.persistence.*;

/**
 * @author Graham Willis
 * The persistent class for the unique_identifiers database table.
 *
 */
@Entity
@Table(name = "unique_identifier_aliases")
@GenericGenerator(name = "idGenerator", strategy = "org.hibernate.id.enhanced.SequenceStyleGenerator", parameters = {
        @org.hibernate.annotations.Parameter(name = "sequence_name", value = "unique_identifier_aliases_id_seq") }
)
public class UniqueIdentifierAlias implements ControlledListEntity {

    @JsonIgnore
    @Id
    @GeneratedValue(generator = "idGenerator")
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
    public uk.gov.ea.datareturns.domain.model.fields.impl.EaId getFieldValue() {
        return new uk.gov.ea.datareturns.domain.model.fields.impl.EaId(uniqueIdentifier.getName());
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