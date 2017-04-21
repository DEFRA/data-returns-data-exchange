package uk.gov.ea.datareturns.domain.jpa.entities.userdata;

import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;

/**
 * @author Graham Willis
 */
@Entity
@Table(name = "status")
@GenericGenerator(name = "idGenerator", strategy = "org.hibernate.id.enhanced.SequenceStyleGenerator", parameters = {
        @org.hibernate.annotations.Parameter(name = "sequence_name", value = "status_id_seq") }
)
public class RecordStatus {

    @Id @GeneratedValue(generator = "idGenerator")
    private Long id;

    @Basic
    @Column(name = "status", nullable = false, length = 80)
    private String status;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        RecordStatus that = (RecordStatus) o;

        return status.equals(that.status);

    }

    @Override
    public int hashCode() {
        return status.hashCode();
    }
}
