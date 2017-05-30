package uk.gov.ea.datareturns.domain.jpa.entities.userdata.impl;

import javax.persistence.*;
import java.util.Set;

/**
 * @author Graham Willis
 */
@Entity
@Table(name = "validation_errors")
public class ValidationError {
    @EmbeddedId private ValidationErrorId id;
    @Basic private String message;

    @ManyToOne(optional=false)
    @JoinColumns( {
            @JoinColumn(name = "payload_type", referencedColumnName = "payload_type"),
            @JoinColumn(name = "field_name", referencedColumnName = "field_name")
    })
    private Set<Field> fields;

    public ValidationErrorId getId() {
        return id;
    }

    public void setId(ValidationErrorId id) {
        this.id = id;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public Set<Field> getFields() {
        return fields;
    }

    public void setFields(Set<Field> fields) {
        this.fields = fields;
    }

    @Override
    public String toString() {
        return "ValidationError{" +
                "id=" + id +
                ", message='" + message + '\'' +
                ", fields=" + fields +
                '}';
    }
}
