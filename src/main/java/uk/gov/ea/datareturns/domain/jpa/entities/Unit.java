package uk.gov.ea.datareturns.domain.jpa.entities;

import com.fasterxml.jackson.annotation.JsonIgnore;
import org.hibernate.annotations.GenericGenerator;
import uk.gov.ea.datareturns.domain.jpa.hierarchy.Hierarchy;

import javax.persistence.*;
import java.util.Set;

/**
 * @author Sam Gardner-Dell
 * The persistent class for the units database table.
 *
 */
@Entity
@Table(name = "units")
@GenericGenerator(name = "idGenerator", strategy = "org.hibernate.id.enhanced.SequenceStyleGenerator", parameters = {
        @org.hibernate.annotations.Parameter(name = "sequence_name", value = "units_id_seq") }
)
public class Unit implements AliasingEntity, Hierarchy.GroupedHierarchyEntity {
    @JsonIgnore
    @Id
    @GeneratedValue(generator = "idGenerator")
    private Long id;

    @Basic
    @Column(name = "name", nullable = false, length = 10)
    private String name;

    @Basic
    @JsonIgnore
    @Column(name = "preferred", length = 10)
    private String preferred;

    @Basic
    @Column(name = "long_name", length = 50)
    private String longName;

    @Basic
    @Column(name = "unicode", length = 50)
    private String unicode;

    @Basic
    @Column(name = "description", length = 200)
    private String description;

    @Basic
    @Column(name = "type", length = 50)
    private String type;

    @Transient
    private Set<String> aliases = null;

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
    public String getPreferred() {
        return this.preferred;
    }

    @Override
    public void setPreferred(String preferred) {
        this.preferred = preferred;
    }

    public String getLongName() {
        return longName;
    }

    public void setLongName(String longName) {
        this.longName = longName;
    }

    public String getUnicode() {
        return unicode;
    }

    public void setUnicode(String unicode) {
        this.unicode = unicode;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
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

        Unit that = (Unit) o;

        return name.equals(that.name);
    }

    @Override
    public int hashCode() {
        return name.hashCode();
    }

    @Override
    public String getGroup() {
        return type;
    }
}