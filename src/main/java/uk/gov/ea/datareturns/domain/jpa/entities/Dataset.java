package uk.gov.ea.datareturns.domain.jpa.entities;

import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;

/**
 * @author Graham
 */
@Entity
@Table(name = "datasets")
@GenericGenerator(name = "idGenerator", strategy = "org.hibernate.id.enhanced.SequenceStyleGenerator", parameters = {
        @org.hibernate.annotations.Parameter(name = "sequence_name", value = "datasets_id_seq") }
)
public class Dataset {

    @Id @GeneratedValue(generator = "idGenerator")
    private Long id;

    @Basic @Column(name = "identifier", nullable = false, length = 80)
    private String identifier;

    @Basic @Column(name = "filename", nullable = false, length = 500)
    private String filename;

    @ManyToOne(optional=false)
    @JoinColumn(name = "user_id", referencedColumnName = "id")
    private User user;

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
}
