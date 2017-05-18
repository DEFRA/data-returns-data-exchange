package uk.gov.ea.datareturns.domain.jpa.entities.masterdata.impl;

import com.fasterxml.jackson.annotation.JsonIgnore;
import org.hibernate.annotations.GenericGenerator;
import uk.gov.ea.datareturns.domain.jpa.entities.masterdata.AliasingEntity;
import uk.gov.ea.datareturns.domain.jpa.hierarchy.Hierarchy;

import javax.persistence.*;
import java.util.Set;

/**
 * The persistent class for the parameters database table.
 *
 */
@Entity
@Table(name = "parameters")
@GenericGenerator(name = "idGenerator", strategy = "org.hibernate.id.enhanced.SequenceStyleGenerator", parameters = {
        @org.hibernate.annotations.Parameter(name = "sequence_name", value = "parameters_id_seq") }
)
public class Parameter implements AliasingEntity, Hierarchy.HierarchyEntity {

    @Id
    @GeneratedValue(generator = "idGenerator")
    @JsonIgnore
    private Long id;

    @Basic
    @Column(name = "name", nullable = false, length = 150)
    private String name;

    @Basic
    @Column(name = "cas", length = 50)
    private String cas;

    @JsonIgnore
    @Basic
    @Column(name = "preferred", length = 150)
    private String preferred;

    @JsonIgnore
    @Basic
    @Column(name = "type", length = 100)
    private String type;

    @Transient
    Set<String> aliases = null;

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

    @Override
    public String getPreferred() {
        return preferred;
    }

    @Override
    public void setPreferred(String preferred) {
        this.preferred = preferred;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getCas() {
        return cas;
    }

    public void setCas(String cas) {
        this.cas = cas;
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

        Parameter that = (Parameter) o;

        return name.equals(that.name);
    }

    @Override
    public int hashCode() {
        return name.hashCode();
    }

    @Override
    public String toString() {
        return "Parameter{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", cas='" + cas + '\'' +
                ", preferred='" + preferred + '\'' +
                ", type='" + type + '\'' +
                ", aliases=" + aliases +
                '}';
    }
}