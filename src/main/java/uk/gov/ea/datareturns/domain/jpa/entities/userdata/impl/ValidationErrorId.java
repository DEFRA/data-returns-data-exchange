package uk.gov.ea.datareturns.domain.jpa.entities.userdata.impl;

import javax.persistence.*;

/**
 * @author Graham Willis
 */
@Embeddable
public class ValidationErrorId {
    @ManyToOne(optional=false)
    @JoinColumn(name = "payload_type", referencedColumnName = "palyload_type_name")
    private PayloadType payloadType;

    @Basic @Column(name = "error")
    private String error;

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
}
