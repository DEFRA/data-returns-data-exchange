package uk.gov.ea.datareturns.domain.jpa.dao.masterdata;

import uk.gov.ea.datareturns.domain.jpa.entities.masterdata.ControlledListEntity;

import java.util.List;

/**
 * Interface to provide search functionality for any controlled list entity
 * @param <E>
 * @author Sam Gardner-Dell
 */
@FunctionalInterface
public interface SearchFunction<E extends ControlledListEntity> {
    /**
     * Determine if an entity matches any of the given terms
     *
     * @param entity the entity to search
     * @param terms a list of terms to search for
     * @return true if the entity matches, false otherwise
     */
    boolean matches(E entity, List<String> terms);
}
