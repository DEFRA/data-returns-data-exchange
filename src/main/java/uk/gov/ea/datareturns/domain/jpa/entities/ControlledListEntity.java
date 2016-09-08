package uk.gov.ea.datareturns.domain.jpa.entities;

/**
 * Created by graham on 26/07/16.
 *
 * An interface to designate an enity as a controlled list. These entities are members
 * of ControlledListsList. Controlled lists
 * can be requested gby a REST api call
 */
public interface ControlledListEntity {
    Long getId();

    void setId(final Long id);

    String getName();

    void setName(final String name);

}
