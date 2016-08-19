package uk.gov.ea.datareturns.domain.jpa.entities;

/**
 * Created by graham on 26/07/16.
 * This interface indicates that implementing entities are persisted by hibernate
 * and have a Dao that extends AbstractJpaDao
 */
public interface PersistedEntity {
    Long getId();
    void setId(final Long id);

    String getName();
    void setName(final String name);
}
