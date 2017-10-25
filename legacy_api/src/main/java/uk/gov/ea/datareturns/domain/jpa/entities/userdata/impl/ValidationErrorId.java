package uk.gov.ea.datareturns.domain.jpa.entities.userdata.impl;

import javax.persistence.*;
import java.io.Serializable;

/**
 * @author Graham Willis
 */
@Embeddable
public class ValidationErrorId implements Serializable {
    @ManyToOne(optional=false)
    @JoinColumn(name = "payload_type", referencedColumnName = "payload_type")
    private PayloadType payloadType;

    @Basic @Column(name = "error")
    private String error;

    public ValidationErrorId() {

    }

    public ValidationErrorId(PayloadType payloadType, String error) {
        this.payloadType = payloadType;
        this.error = error;
    }

    public PayloadType getPayloadType() {
        return payloadType;
    }

    public void setPayloadType(PayloadType payloadType) {
        this.payloadType = payloadType;
    }

    public String getError() {
        return error;
    }

    public void setError(String error) {
        this.error = error;
    }

    @Override
    public String toString() {
        return "ValidationErrorId{" +
                "payloadType=" + payloadType +
                ", error='" + error + '\'' +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        ValidationErrorId that = (ValidationErrorId) o;

        if (payloadType != null ? !payloadType.equals(that.payloadType) : that.payloadType != null) return false;
        return error != null ? error.equals(that.error) : that.error == null;

    }

    @Override
    public int hashCode() {
        int result = payloadType != null ? payloadType.hashCode() : 0;
        result = 31 * result + (error != null ? error.hashCode() : 0);
        return result;
    }
}
