package uk.gov.ea.datareturns.domain.jpa.entities;

import com.fasterxml.jackson.annotation.JsonIgnore;

import javax.persistence.*;
import java.util.Set;

/**
 * The persistent class for the reference_periods database table.
 *
 */
@SuppressWarnings({ "JavaDoc", "unused" })
@Entity
@Table(name = "reference_periods")
public class ReferencePeriod implements ControlledListEntity, AliasingEntity {

    @JsonIgnore
    @Id
    @SequenceGenerator(name = "reference_periods_id_seq", sequenceName = "reference_periods_id_seq", allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "reference_periods_id_seq")
    private Long id;

    @Basic
    @Column(name = "name", nullable = false, length = 100)
    private String name;

    @Basic
    @Column(name = "notes", length = 250)
    private String notes;

    @Basic
    @JsonIgnore
    @Column(name = "preferred", length = 100)
    private String preferred;

    @Transient
    private Set<String> aliases = null;

    @Override
    public Long getId() {
        return id;
    }

    @Override
    public void setId(Long id) {
        this.id = id;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public void setName(String name) {
        this.name = name;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String description) {
        this.notes = description;
    }

    public String getPreferred() {
        return preferred;
    }

    @Override
    public void setPreferred(String preferred) {
        this.preferred = preferred;
    }

    @Override
    public Set<String> getAliases() {
        return this.aliases;
    }

    @Override
    public void setAliases(Set<String> aliases) {
        this.aliases = aliases;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        ReferencePeriod that = (ReferencePeriod) o;

        return name.equals(that.name);
    }

    @Override
    public int hashCode() {
        return name.hashCode();
    }

}