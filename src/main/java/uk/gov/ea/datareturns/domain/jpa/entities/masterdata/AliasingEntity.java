package uk.gov.ea.datareturns.domain.jpa.entities.masterdata;

import com.fasterxml.jackson.annotation.JsonIgnore;

import java.util.Set;

/**
 * Created by graham on 16/08/16.
 */
public interface AliasingEntity extends ControlledListEntity {
    String getPreferred();

    void setPreferred(String preferred);

    Set<String> getAliases();

    void setAliases(Set<String> aliases);

    @JsonIgnore
    default boolean isAlias() {
        return getPreferred() != null;
    }

    @JsonIgnore
    default boolean isPrimary() {
        return getPreferred() == null;
    }
}
