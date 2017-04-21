package uk.gov.ea.datareturns.domain.jpa.entities.masterdata.impl;

import com.fasterxml.jackson.annotation.JsonIgnore;
import org.hibernate.annotations.GenericGenerator;
import uk.gov.ea.datareturns.domain.jpa.entities.masterdata.ControlledListEntity;

import javax.persistence.*;

/**
 * The persistent class for the qualifiers database table.
 *
 */
@Entity
@Table(name = "qualifiers")
@GenericGenerator(name = "idGenerator", strategy = "org.hibernate.id.enhanced.SequenceStyleGenerator", parameters = {
        @org.hibernate.annotations.Parameter(name = "sequence_name", value = "qualifiers_id_seq") }
)
public class Qualifier implements ControlledListEntity {

    @Id
    @GeneratedValue(generator = "idGenerator")
    @JsonIgnore
    private Long id;

    @Basic
    @Column(name = "name", nullable = false, length = 100)
    private String name;

    @Basic
    @Column(name = "notes", length = 100)
    private String notes;

    @Basic
    @Column(name = "type", length = 50)
    private String type;

    @Basic
    @Column(name = "singleormultiple", length = 20)
    private String SingleOrMultiple;

    public Long getId() {
        return this.id;
    }

    public void setId(final Long id) {
        this.id = id;
    }

    @Override
    public String getName() {
        return this.name;
    }

    public void setName(final String name) {
        this.name = name;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String description) {
        this.notes = description;
    }

    public String getType() {
        return type;
    }

    public void setType(String suggested_category) {
        this.type = suggested_category;
    }

    public String getSingleOrMultiple() {
        return SingleOrMultiple;
    }

    public void setSingleOrMultiple(String singleOrMultiple) {
        SingleOrMultiple = singleOrMultiple;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Qualifier that = (Qualifier) o;

        return name.equals(that.name);
    }

    @Override
    public int hashCode() {
        return name.hashCode();
    }
}