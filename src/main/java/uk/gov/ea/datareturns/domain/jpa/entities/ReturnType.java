package uk.gov.ea.datareturns.domain.jpa.entities;

import com.fasterxml.jackson.annotation.JsonIgnore;
import uk.gov.ea.datareturns.domain.jpa.dao.EntityDao;
import uk.gov.ea.datareturns.domain.jpa.dao.ReturnTypeDao;
import uk.gov.ea.datareturns.domain.jpa.entities.hierarchy.Hierarchy;

import javax.persistence.*;

/**
 * The persistent class for the return_types database table.
 *
 */
@SuppressWarnings({ "JavaDoc", "unused" })
@Entity
@Table(name = "return_types")
public class ReturnType implements ControlledListEntity, Hierarchy.HierarchyEntity {

    @Id
    @SequenceGenerator(name = "return_types_id_seq", sequenceName = "return_types_id_seq", allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "return_types_id_seq")
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

    @Override
    public Class<? extends EntityDao> getEntityDao() {
        return ReturnTypeDao.class;
    }
}