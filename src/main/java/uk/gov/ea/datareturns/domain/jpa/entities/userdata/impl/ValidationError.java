package uk.gov.ea.datareturns.domain.jpa.entities.userdata.impl;

import javax.persistence.*;
import java.util.Set;

/**
 * @author Graham Willis
 */
@Entity
@Table(name = "validation_errors")
public class ValidationError {
    @Id String error;

    @Basic String message;

    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "validation_error_fields", joinColumns = @JoinColumn(name = "error"))
    @Column(name = "field")
    Set<String> fields;

    public String getError() {
        return error;
    }

    public void setError(String error) {
        this.error = error;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public Set<String> getFields() {
        return fields;
    }

    public void setFields(Set<String> fields) {
        this.fields = fields;
    }

    @Override
    public String toString() {
        return "ValidationError{" +
                "error='" + error + '\'' +
                "message='" + message + '\'' +
                '}';
    }
}
