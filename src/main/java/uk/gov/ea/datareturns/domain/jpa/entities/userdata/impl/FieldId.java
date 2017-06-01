package uk.gov.ea.datareturns.domain.jpa.entities.userdata.impl;

import javax.persistence.*;
import java.io.Serializable;

/**
 * @author Graham Willis
 * Primary key class for field
 */
@Embeddable
public class FieldId implements Serializable {
    @Basic
    @Column(name = "field_name")
    private String fieldName;

    @ManyToOne(optional=false)
    @JoinColumn(name = "payload_type", referencedColumnName = "payload_type")
    private PayloadType payloadType;

    public String getFieldName() {
        return fieldName;
    }

    public void setFieldName(String fieldName) {
        this.fieldName = fieldName;
    }

    public PayloadType getPayloadType() {
        return payloadType;
    }

    public void setPayloadType(PayloadType payloadType) {
        this.payloadType = payloadType;
    }

    @Override
    public String toString() {
        return "FieldId{" +
                "fieldName='" + fieldName + '\'' +
                ", payloadType=" + payloadType +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        FieldId fieldId = (FieldId) o;

        if (fieldName != null ? !fieldName.equals(fieldId.fieldName) : fieldId.fieldName != null) return false;
        return payloadType != null ? payloadType.equals(fieldId.payloadType) : fieldId.payloadType == null;

    }

    @Override
    public int hashCode() {
        int result = fieldName != null ? fieldName.hashCode() : 0;
        result = 31 * result + (payloadType != null ? payloadType.hashCode() : 0);
        return result;
    }
}
