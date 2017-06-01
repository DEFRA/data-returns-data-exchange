package uk.gov.ea.datareturns.domain.jpa.entities.userdata.impl;

import javax.persistence.*;

/**
 * @author Graham Willis
 */
@Entity
@Table(name = "payload_types")
public class PayloadType {

    @Id @Basic @Column(name = "payload_type")
    private String payloadTypeName;

    public String getPayloadTypeName() {
        return payloadTypeName;
    }

    public void setPayloadTypeName(String payloadTypeName) {
        this.payloadTypeName = payloadTypeName;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        PayloadType that = (PayloadType) o;

        return payloadTypeName != null ? payloadTypeName.equals(that.payloadTypeName) : that.payloadTypeName == null;
    }

    @Override
    public int hashCode() {
        return payloadTypeName != null ? payloadTypeName.hashCode() : 0;
    }

    @Override
    public String toString() {
        return "PayloadType{" +
                "payloadTypeName='" + payloadTypeName + '\'' +
                '}';
    }
}
