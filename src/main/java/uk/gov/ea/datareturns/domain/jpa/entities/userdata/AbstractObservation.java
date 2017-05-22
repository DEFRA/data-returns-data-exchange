package uk.gov.ea.datareturns.domain.jpa.entities.userdata;

import uk.gov.ea.datareturns.domain.jpa.entities.userdata.impl.RecordEntity;

import javax.persistence.*;
import java.util.HashSet;
import java.util.Set;

/**
 * @author Graham Willis
 */
@Entity
@Inheritance(strategy = InheritanceType.TABLE_PER_CLASS)
public abstract class AbstractObservation implements Userdata {

    @Id
    @OneToOne(optional = false)
    @JoinColumn(name = "record_id", referencedColumnName = "id")
    private RecordEntity recordEntity;

    public RecordEntity getRecordEntity() {
        return recordEntity;
    }
    public void setRecordEntity(RecordEntity recordEntity) {
        this.recordEntity = recordEntity;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        AbstractObservation that = (AbstractObservation) o;

        return recordEntity.equals(that.recordEntity);
    }

    @Override
    public int hashCode() {
        return recordEntity.hashCode();
    }

    @Transient
    private Set<EntitySubstitution> entitySubstitutions = new HashSet<>();

    public static class EntitySubstitution {
        private final String entity;
        private final String submitted;
        private final String preferred;

        public EntitySubstitution(String entity, String submitted, String preferred) {
            this.entity = entity;
            this.submitted = submitted;
            this.preferred = preferred;
        }

        public String getEntity() {
            return entity;
        }

        public String getSubmitted() {
            return submitted;
        }

        public String getPreferred() {
            return preferred;
        }
    }

    public void addSubstution(String entity, String submitted, String preferred) {
        entitySubstitutions.add(new EntitySubstitution(entity, submitted, preferred));
    }

    public Set<EntitySubstitution> getEntitySubstitutions() {
        return entitySubstitutions;
    }
}
