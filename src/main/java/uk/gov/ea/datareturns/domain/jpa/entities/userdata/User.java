package uk.gov.ea.datareturns.domain.jpa.entities.userdata;

import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;

/**
 * @author Graham
 */
@Entity
@Table(name = "users")
@GenericGenerator(name = "idGenerator", strategy = "org.hibernate.id.enhanced.SequenceStyleGenerator", parameters = {
        @org.hibernate.annotations.Parameter(name = "sequence_name", value = "users_id_seq") }
)
public class User {

    @Id @GeneratedValue(generator = "idGenerator")
    private Long id;

    @Basic @Column(name = "name", nullable = false, length = 80)
    private String name;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        User user = (User) o;

        return name.equals(user.name);
    }

    @Override
    public int hashCode() {
        return name.hashCode();
    }
}
