package uk.gov.ea.datareturns.domain.jpa.entities.masterdata;

import com.fasterxml.jackson.annotation.JsonIgnore;

import java.io.Serializable;

/**
 * Created by graham on 26/07/16.
 *
 * An interface to designate an entity as a controlled list. These entities are members
 * of ControlledListsList. Controlled lists
 * can be requested gby a REST api call
 */
public interface MasterDataEntity extends Serializable {
    Long getId();

    String getName();

    void setName(final String name);

    @JsonIgnore
    default boolean isAlias() {
        return false;
    }

    @JsonIgnore
    default boolean isPrimary() {
        return true;
    }
}
