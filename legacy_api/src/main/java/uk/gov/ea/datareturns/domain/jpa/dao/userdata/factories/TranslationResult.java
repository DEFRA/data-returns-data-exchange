package uk.gov.ea.datareturns.domain.jpa.dao.userdata.factories;

import java.util.LinkedHashSet;
import java.util.Set;

public class TranslationResult<M> {
    private M entity;
    private Set<EntitySubstitution> substitutions;

    public M getEntity() {
        return entity;
    }

    public void setEntity(M entity) {
        this.entity = entity;
    }

    public Set<EntitySubstitution> getSubstitutions() {
        return substitutions;
    }

    public void setSubstitutions(
            Set<EntitySubstitution> substitutions) {
        this.substitutions = substitutions;
    }

    public void addSubstitution(String entity, String submitted, String preferred) {
        addSubstitution(new EntitySubstitution(entity, submitted, preferred));
    }

    public void addSubstitution(EntitySubstitution substitution) {
        if (this.substitutions == null) {
            this.substitutions = new LinkedHashSet<>();
        }
        this.substitutions.add(substitution);
    }
}
