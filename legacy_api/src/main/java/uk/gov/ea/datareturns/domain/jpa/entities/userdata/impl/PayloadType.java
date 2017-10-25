package uk.gov.ea.datareturns.domain.jpa.entities.userdata.impl;

import javax.persistence.*;
import java.io.Serializable;
import java.time.Instant;

/**
 * @author Graham Willis
 */
@Entity
@Table(name = "md_payload_types")
public class PayloadType implements Serializable {

    @Id @Column(name = "payload_type")
    private String payloadTypeName;

    public String getPayloadTypeName() {
        return payloadTypeName;
    }

    public void setPayloadTypeName(String payloadTypeName) {
        this.payloadTypeName = payloadTypeName;
    }

    @Basic @Column(name = "create_date", nullable = false)
    private Instant createDate;

    @Basic @Column(name = "last_changed_date", nullable = false)
    private Instant lastChangedDate;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        PayloadType that = (PayloadType) o;

        return payloadTypeName != null ? payloadTypeName.equals(that.payloadTypeName) : that.payloadTypeName == null;
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
