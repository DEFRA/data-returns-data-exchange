package uk.gov.ea.datareturns.domain.jpa.entities.masterdata.impl;

import com.fasterxml.jackson.annotation.JsonIgnore;
import org.hibernate.annotations.GenericGenerator;
import uk.gov.ea.datareturns.domain.jpa.entities.masterdata.AliasingEntity;
import uk.gov.ea.datareturns.domain.model.fields.AbstractEntityValue;
import uk.gov.ea.datareturns.domain.model.fields.FieldValue;

import javax.persistence.*;
import java.util.Set;

/**
 * The persistent class for the units database table.
 *
 */
@Entity
@Table(name = "text_values")
@GenericGenerator(name = "idGenerator", strategy = "org.hibernate.id.enhanced.SequenceStyleGenerator", parameters = {
        @org.hibernate.annotations.Parameter(name = "sequence_name", value = "text_values_id_seq") }
)
public class TextValue implements AliasingEntity {

    @Id
    @GeneratedValue(generator = "idGenerator")
    @JsonIgnore
    private Long id;

    @Basic
    @Column(name = "name", nullable = false, length = 30)
    private String name;

    @JsonIgnore
    @Basic
    @Column(name = "preferred", length = 30)
    private String preferred;

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
    public uk.gov.ea.datareturns.domain.model.fields.impl.TxtValue getFieldValue() {
        return new uk.gov.ea.datareturns.domain.model.fields.impl.TxtValue(this.getName());
    }

    @Override
    public String getPreferred() {
        return this.preferred;
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

        TextValue that = (TextValue) o;

        return name.equals(that.name);
    }

    @Override
    public int hashCode() {
        return name.hashCode();
    }

    @Override
    public String toString() {
        return "TextValue{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", preferred='" + preferred + '\'' +
                ", aliases=" + aliases +
                '}';
    }
}