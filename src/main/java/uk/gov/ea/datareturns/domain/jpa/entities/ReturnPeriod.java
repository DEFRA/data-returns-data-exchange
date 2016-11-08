package uk.gov.ea.datareturns.domain.jpa.entities;

import com.fasterxml.jackson.annotation.JsonIgnore;

import javax.persistence.*;

/**
 * The persistent class for the return_periods database table.
 *
 */
@SuppressWarnings({ "JavaDoc", "unused" })
@Entity
@Table(name = "return_periods")
public class ReturnPeriod implements ControlledListEntity {
    @Id
    @SequenceGenerator(name = "return_periods_id_seq", sequenceName = "return_periods_id_seq", allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "return_periods_id_seq")
    @JsonIgnore
    private Long id;

    @Basic
    @Column(name = "name", nullable = false, length = 20)
    private String name;

    @Basic
    @Column(name = "definition", nullable = false, length = 600)
    public String definition;

    @Basic
    @Column(name = "example", nullable = false, length = 20)
    public String example;

    public Long getId() {
        return this.id;
    }

    public void setId(final Long id) {
        this.id = id;
    }

    public String getName() {
        return this.name;
    }

    public void setName(final String name) {
        this.name = name;
    }

    public String getDefinition() {
        return definition;
    }

    public void setDefinition(String description) {
        this.definition = description;
    }

    public String getExample() {
        return example;
    }

    public void setExample(String example) {
        this.example = example;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        ReturnPeriod that = (ReturnPeriod) o;

        return name.equals(that.name);
    }

    @Override
    public int hashCode() {
        return name.hashCode();
    }
}