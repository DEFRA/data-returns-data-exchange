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
public abstract class AbstractPayloadEntity implements Userdata {

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

        AbstractPayloadEntity that = (AbstractPayloadEntity) o;

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

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;

            EntitySubstitution that = (EntitySubstitution) o;

            if (entity != null ? !entity.equals(that.entity) : that.entity != null) return false;
            if (submitted != null ? !submitted.equals(that.submitted) : that.submitted != null) return false;
            return preferred != null ? preferred.equals(that.preferred) : that.preferred == null;
        }

        @Override
        public int hashCode() {
            int result = entity != null ? entity.hashCode() : 0;
            result = 31 * result + (submitted != null ? submitted.hashCode() : 0);
            result = 31 * result + (preferred != null ? preferred.hashCode() : 0);
            return result;
        }
    }

    public void addSubstution(String entity, String submitted, String preferred) {
        entitySubstitutions.add(new EntitySubstitution(entity, submitted, preferred));
    }

    public Set<EntitySubstitution> getEntitySubstitutions() {
        return entitySubstitutions;
    }
}
