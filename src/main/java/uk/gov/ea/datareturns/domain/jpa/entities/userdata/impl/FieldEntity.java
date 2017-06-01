package uk.gov.ea.datareturns.domain.jpa.entities.userdata.impl;

import javax.persistence.*;

/**
 * @author Graham Willis
 */
@Entity
@Table(name = "fields")
public class FieldEntity {
    @EmbeddedId FieldId id;

    @Basic public String description;

    public FieldId getId() {
        return id;
    }

    public void setId(FieldId id) {
        this.id = id;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    @Override
    public String toString() {
        return "FieldEntity{" +
                "id=" + id +
                '}';
    }
}
