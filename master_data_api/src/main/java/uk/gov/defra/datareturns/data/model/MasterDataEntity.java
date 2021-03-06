package uk.gov.defra.datareturns.data.model;

import java.io.Serializable;

/**
 * Created by graham on 26/07/16.
 * <p>
 * An interface to designate an entity as a controlled list. These entities are members
 * of ControlledListsList. Controlled lists
 * can be requested gby a REST api call
 */
public interface MasterDataEntity extends Serializable {
    Long getId();

    String getNomenclature();

    void setNomenclature(final String nomenclature);
}
