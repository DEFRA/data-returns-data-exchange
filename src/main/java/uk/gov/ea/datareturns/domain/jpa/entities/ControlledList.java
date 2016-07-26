package uk.gov.ea.datareturns.domain.jpa.entities;

/**
 * Created by graham on 26/07/16.
 *
 * An interface to designate an enity as a controlled list. These entities are members
 * of ControlledListsList and are always persisted entities. Controlled lists
 * can be requested gby a REST api call
 */
public interface ControlledList extends PersistedEntity {
}
