package uk.gov.ea.datareturns.domain.jpa.dao.userdata.factories;

import java.util.Objects;

public class EntitySubstitution {
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

    @Override public boolean equals(Object o) {
        if (this == o)
            return true;
        if (o == null || getClass() != o.getClass())
            return false;
        EntitySubstitution that = (EntitySubstitution) o;
        return Objects.equals(entity, that.entity) &&
                Objects.equals(submitted, that.submitted) &&
                Objects.equals(preferred, that.preferred);
    }

    @Override public int hashCode() {
        return Objects.hash(entity, submitted, preferred);
    }
}
