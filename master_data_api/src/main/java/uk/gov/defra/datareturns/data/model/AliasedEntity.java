package uk.gov.defra.datareturns.data.model;

import java.util.Set;

/**
 * Created by graham on 16/08/16.
 */
public interface AliasedEntity<E extends MasterDataEntity> extends MasterDataEntity {
    Set<E> getAliases();
}
