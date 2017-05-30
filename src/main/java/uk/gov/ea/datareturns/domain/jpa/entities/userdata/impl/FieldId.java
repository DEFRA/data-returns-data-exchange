package uk.gov.ea.datareturns.domain.jpa.entities.userdata.impl;

import javax.persistence.*;

/**
 * @author Graham Willis
 * Primary key class for field
 */
@Embeddable
public class FieldId {
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

    @Override
    public String toString() {
        return "FieldId{" +
                "fieldName='" + fieldName + '\'' +
                ", payloadType=" + payloadType +
                '}';
    }
}
