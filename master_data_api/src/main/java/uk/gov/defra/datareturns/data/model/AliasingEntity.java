package uk.gov.defra.datareturns.data.model;

import com.fasterxml.jackson.annotation.JsonIgnore;

/**
 * Created by graham on 16/08/16.
 */
public interface AliasingEntity<E extends MasterDataEntity> extends MasterDataEntity {
    E getPreferred();

    void setPreferred(E preferred);

    //    Set<E> getAliases();
    //
    //    void setAliases(Set<E> aliases);

    @JsonIgnore
    default boolean isAlias() {
        return getPreferred() != null;
    }

    @JsonIgnore
    default boolean isPrimary() {
        return getPreferred() == null;
    }
}
