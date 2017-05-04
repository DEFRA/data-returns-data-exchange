package uk.gov.ea.datareturns.domain.jpa.entities.userdata.impl;

import org.hibernate.annotations.GenericGenerator;
import uk.gov.ea.datareturns.domain.jpa.entities.userdata.Metadata;
import uk.gov.ea.datareturns.domain.jpa.entities.userdata.Userdata;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Collection;

/**
 * @author Graham
 */
@Entity
@Table(name = "datasets")
@GenericGenerator(name = "idGenerator", strategy = "org.hibernate.id.enhanced.SequenceStyleGenerator", parameters = {
        @org.hibernate.annotations.Parameter(name = "sequence_name", value = "datasets_id_seq") }
)
public class Dataset implements Metadata {

    @Id @GeneratedValue(generator = "idGenerator")
    private Long id;

    @Basic @Column(name = "identifier", nullable = false, length = 80)
    private String identifier;

    @Basic @Column(name = "filename", nullable = false, length = 500)
    private String filename;

    @ManyToOne(optional=false)
    @JoinColumn(name = "user_id", referencedColumnName = "id")
    private User user;

    @OneToMany(mappedBy="dataset",targetEntity=Record.class, fetch=FetchType.LAZY, cascade = CascadeType.REMOVE)
    private Collection records;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getIdentifier() {
        return identifier;
    }

    public void setIdentifier(String identifier) {
        this.identifier = identifier;
    }

    public String getFilename() {
        return filename;
    }

    public void setFilename(String filename) {
        this.filename = filename;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public Collection getRecords() {
        return records;
    }

    public void setRecords(Collection records) {
        this.records = records;
    }

    /*
     * The dataset identifier is unique for a given user
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Dataset dataset = (Dataset) o;

        if (!identifier.equals(dataset.identifier)) return false;
        return user.equals(dataset.user);

    }

    @Override
    public int hashCode() {
        int result = identifier.hashCode();
        result = 31 * result + user.hashCode();
        return result;
    }

    @Override
    public String toString() {
        return "Dataset{" +
                "id=" + id +
                ", identifier='" + identifier + '\'' +
                ", filename='" + filename + '\'' +
                ", user=" + user +
                '}';
    }
}
