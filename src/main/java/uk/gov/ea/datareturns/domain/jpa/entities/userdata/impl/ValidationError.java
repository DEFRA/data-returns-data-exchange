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

    /*
     * Currently does not support maintaining the relationship because the payload_type is shared
     * by both sides of the association. This means we cannot change the field side of the relationship.
     * (To do this would require duplication of the payload_type field in the table)
     */
    @ManyToMany
    @JoinTable(name = "validation_error_fields",
            joinColumns = {
                @JoinColumn(name = "payload_type", referencedColumnName = "payload_type"),
                @JoinColumn(name = "error", referencedColumnName = "error")
            }, inverseJoinColumns = {
                @JoinColumn(name = "payload_type", referencedColumnName = "payload_type", insertable = false,  updatable = false),
                @JoinColumn(name = "field_name", referencedColumnName = "field_name")
        }
    )
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
