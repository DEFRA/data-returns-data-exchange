package uk.gov.ea.datareturns.domain.jpa.entities;

import com.fasterxml.jackson.annotation.JsonIgnore;

import javax.persistence.*;

/**
 * The persistent class for the qualifiers database table.
 *
 */
@SuppressWarnings({ "JavaDoc", "unused" })
@Entity
@Table(name = "qualifiers")
public class Qualifier implements ControlledListEntity {

    @JsonIgnore
    @Id
    @SequenceGenerator(name = "qualifiers_id_seq", sequenceName = "qualifiers_id_seq", allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "qualifiers_id_seq")
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
    @JsonIgnore
    public ControlledListsList getControlledListType() {
        return ControlledListsList.QUALIFIERS;
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