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
}
