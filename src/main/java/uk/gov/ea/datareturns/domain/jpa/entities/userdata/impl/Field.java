package uk.gov.ea.datareturns.domain.jpa.entities.userdata.impl;

import javax.persistence.*;

/**
 * @author Graham Willis
 */
@Entity
@Table(name = "fields")
public class Field {
    @EmbeddedId FieldId id;

    public FieldId getId() {
        return id;
    }

    public void setId(FieldId id) {
        this.id = id;
    }

    @Override
    public String toString() {
        return "Field{" +
                "id=" + id +
                '}';
    }
}
