
package uk.gov.ea.datareturns.domain.jpa.entities.userdata.impl;
import javax.persistence.*;
import java.io.Serializable;
import java.time.Instant;
import java.util.Set;

/**
 * @author Graham Willis
 */
@Entity
@Table(name = "md_validation_errors")
public class ValidationError implements Serializable {
    @EmbeddedId private ValidationErrorId id;
    @Basic private String message;

    /*
     * Currently does not support maintaining the relationship because the payload_type is shared
     * by both sides of the association. This means we cannot change the field side of the relationship.
     * (To do this would require duplication of the payload_type field in the table)
     */
    @ManyToMany
    @JoinTable(name = "md_validation_error_fields",
            joinColumns = {
                @JoinColumn(name = "payload_type", referencedColumnName = "payload_type"),
                @JoinColumn(name = "error", referencedColumnName = "error")
            }, inverseJoinColumns = {
                @JoinColumn(name = "payload_type", referencedColumnName = "payload_type", insertable = false,  updatable = false),
                @JoinColumn(name = "field_name", referencedColumnName = "field_name")
        }
    )
    private Set<FieldEntity> fields;

    @Basic @Column(name = "create_date", nullable = false)
    private Instant createDate;

    @Basic @Column(name = "last_changed_date", nullable = false)
    private Instant lastChangedDate;

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

    public Set<FieldEntity> getFields() {
        return fields;
    }

    public void setFields(Set<FieldEntity> fields) {
        this.fields = fields;
    }

    public Instant getCreateDate() {
        return createDate;
    }

    public void setCreateDate(Instant createDate) {
        this.createDate = createDate;
    }

    public Instant getLastChangedDate() {
        return lastChangedDate;
    }

    public void setLastChangedDate(Instant lastChangedDate) {
        this.lastChangedDate = lastChangedDate;
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
