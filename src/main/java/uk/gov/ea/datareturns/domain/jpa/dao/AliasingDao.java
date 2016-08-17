package uk.gov.ea.datareturns.domain.jpa.dao;

import uk.gov.ea.datareturns.domain.jpa.entities.AliasingEntity;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Predicate;
import java.util.stream.Collectors;

/**
 * Created by graham on 16/08/16.
 */
public class AliasingDao<E extends AliasingEntity> extends AbstractJpaDao {
    /**
     * Let the Dao class know the type of entity in order that type-safe
     * hibernate operations can be performed
     *
     * @param entityClass
     */
    AliasingDao(Class<E> entityClass) {
        super(entityClass);
    }

    /**
     * This override of list() calculates the aliases from the preferred list
     * @return the processed list
     */
    @Override
    public List<E> list() {
        // Get the list from the cached master list in AbstractJpaDao
        List<E> list = super.list();
        return aliasProcessor(list);
    }

    @Override
    public List<E> list(Predicate predicate) {
        List<E> list = super.list(predicate);
        return aliasProcessor(list);
    }

    private List<E> aliasProcessor(List<E> list) {
        // Split the stream into aliases and basis; with and without preferred set
        List<E> basis = list.stream().filter(e -> e.getPreferred() == null).collect(Collectors.toList());
        List<E> aliases = list.stream().filter(e -> e.getPreferred() != null).collect(Collectors.toList());
        // Collect the aliases into groups by preferred name
        Map<String, Set<String>> aliasesByName = aliases.stream().collect(
                Collectors.groupingBy(E::getPreferred,
                        Collectors.mapping(E::getName, Collectors.toSet())
                )
        );
        // Go through the basis list and look up the name in the aliases
        // on aliasesByName and append them to name
        List<E> result = basis.stream().map(e -> {
            Set<String> aliasList = aliasesByName.get(e.getName());
            if (aliasList != null) {
                e.setAliases(aliasList);
            }
            return e;
        }).collect(Collectors.toList());

        return result;
    }

}
