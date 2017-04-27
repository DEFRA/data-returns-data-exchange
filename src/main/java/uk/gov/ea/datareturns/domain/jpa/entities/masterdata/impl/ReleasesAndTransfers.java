package uk.gov.ea.datareturns.domain.jpa.entities.masterdata.impl;

import com.fasterxml.jackson.annotation.JsonIgnore;
import org.hibernate.annotations.GenericGenerator;
import uk.gov.ea.datareturns.domain.jpa.entities.masterdata.ControlledListEntity;
import uk.gov.ea.datareturns.domain.jpa.hierarchy.Hierarchy;
import uk.gov.ea.datareturns.domain.model.fields.AbstractEntityValue;
import uk.gov.ea.datareturns.domain.model.fields.FieldValue;

import javax.persistence.*;

/**
 * The persistent class for the methods_or_standards database table.
 *
 */
@Entity
@Table(name = "releases_and_transfers")
@GenericGenerator(name = "idGenerator", strategy = "org.hibernate.id.enhanced.SequenceStyleGenerator", parameters = {
        @org.hibernate.annotations.Parameter(name = "sequence_name", value = "releases_and_transfers_id_seq") }
)
public class ReleasesAndTransfers implements ControlledListEntity, Hierarchy.HierarchyEntity {

    @Id
    @GeneratedValue(generator = "idGenerator")
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
    public uk.gov.ea.datareturns.domain.model.fields.impl.ReleasesAndTransfers getFieldValue() {
        return new uk.gov.ea.datareturns.domain.model.fields.impl.ReleasesAndTransfers(this.name);
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

    @Override
    public String toString() {
        return "ReleasesAndTransfers{" +
                "id=" + id +
                ", name='" + name + '\'' +
                '}';
    }
}