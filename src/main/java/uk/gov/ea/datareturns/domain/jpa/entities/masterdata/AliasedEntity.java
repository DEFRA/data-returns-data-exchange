package uk.gov.ea.datareturns.domain.jpa.entities.masterdata;

import java.util.Set;

/**
 * Created by graham on 16/08/16.
 */
public interface AliasedEntity<E extends MasterDataEntity> extends MasterDataEntity {
    Set<E> getAliases();

    void setAliases(Set<E> aliases);
}
