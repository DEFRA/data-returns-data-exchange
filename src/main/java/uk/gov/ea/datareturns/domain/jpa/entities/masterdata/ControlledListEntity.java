package uk.gov.ea.datareturns.domain.jpa.entities.masterdata;

import uk.gov.ea.datareturns.domain.validation.model.fields.FieldValue;

/**
 * Created by graham on 26/07/16.
 *
 * An interface to designate an entity as a controlled list. These entities are members
 * of ControlledListsList. Controlled lists
 * can be requested gby a REST api call
 */
public interface ControlledListEntity<T extends FieldValue> {
    Long getId();

    void setId(final Long id);

    String getName();

    void setName(final String name);
}
