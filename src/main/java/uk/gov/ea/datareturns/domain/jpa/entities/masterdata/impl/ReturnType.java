package uk.gov.ea.datareturns.domain.jpa.entities.masterdata.impl;

import com.fasterxml.jackson.annotation.JsonIgnore;
import org.hibernate.annotations.GenericGenerator;
import uk.gov.ea.datareturns.domain.jpa.entities.masterdata.ControlledListEntity;
import uk.gov.ea.datareturns.domain.jpa.hierarchy.Hierarchy;

import javax.persistence.*;

/**
 * The persistent class for the return_types database table.
 *
 */
@Entity
@Table(name = "return_types")
@GenericGenerator(name = "idGenerator", strategy = "org.hibernate.id.enhanced.SequenceStyleGenerator", parameters = {
        @org.hibernate.annotations.Parameter(name = "sequence_name", value = "return_types_id_seq") }
)
public class ReturnType implements ControlledListEntity, Hierarchy.GroupedHierarchyEntity {

    @Id
    @GeneratedValue(generator = "idGenerator")
    @JsonIgnore
    private Long id;

    @Basic
    @Column(name = "name", nullable = false, length = 80)
    private String name;

    @Basic
    @Column(name = "sector", nullable = false, length = 20)
    private String sector;

    public Long getId() {
        return this.id;
    }

    public void setId(final Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getSector() {
        return sector;
    }

    public void setSector(String sector) {
        this.sector = sector;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        ReturnType that = (ReturnType) o;

        return name.equals(that.name);
    }

    @Override
    public int hashCode() {
        return name.hashCode();
    }

    /**
     * Group by sector
     * @return Group
     */
    @Override
    public String getGroup() {
        return sector;
    }

    @Override
    public String toString() {
        return "ReturnType{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", sector='" + sector + '\'' +
                '}';
    }
}