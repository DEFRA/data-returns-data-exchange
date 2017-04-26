package uk.gov.ea.datareturns.domain.jpa.entities.userdata.impl;

import org.hibernate.annotations.GenericGenerator;
import uk.gov.ea.datareturns.domain.jpa.entities.userdata.Userdata;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Collection;

/**
 * @author Graham
 */
@Entity
@Table(name = "users")
@GenericGenerator(name = "idGenerator", strategy = "org.hibernate.id.enhanced.SequenceStyleGenerator", parameters = {
        @org.hibernate.annotations.Parameter(name = "sequence_name", value = "users_id_seq") }
)
public class User implements Serializable, Userdata {

    public static final String SYSTEM = "SYSTEM";

    @Id @GeneratedValue(generator = "idGenerator")
    private Long id;

    @Basic @Column(name = "identifier", nullable = false, length = 80)
    private String identifier;

    @OneToMany(mappedBy="user",targetEntity=Dataset.class, fetch=FetchType.LAZY, cascade = CascadeType.ALL)
    private Collection datasets;

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

    public Collection getDatasets() {
        return datasets;
    }

    public void setDatasets(Collection datasets) {
        this.datasets = datasets;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        User user = (User) o;

        return identifier.equals(user.identifier);
    }

    @Override
    public int hashCode() {
        return identifier.hashCode();
    }

    @Override
    public String toString() {
        return "User{" +
                "id=" + id +
                ", identifier='" + identifier + '\'' +
                '}';
    }
}
