package uk.gov.ea.datareturns.domain.processors;

import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.core.types.dsl.StringPath;
import org.apache.commons.collections4.IterableUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.Pair;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import uk.gov.ea.datareturns.config.JpaRepositoryConfiguration;
import uk.gov.ea.datareturns.domain.dto.impl.ControlledListsDto;
import uk.gov.ea.datareturns.domain.jpa.entities.masterdata.MasterDataEntity;
import uk.gov.ea.datareturns.domain.jpa.entities.masterdata.impl.ControlledListsList;
import uk.gov.ea.datareturns.domain.jpa.hierarchy.Hierarchy;
import uk.gov.ea.datareturns.domain.jpa.hierarchy.HierarchyLevel;
import uk.gov.ea.datareturns.domain.jpa.repositories.BaseRepository;

import java.io.Serializable;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Service to handle operations on controlled lists
 * @author Graham Willis
 */
@SuppressWarnings("unchecked")
@Component
public class ControlledListProcessor {
    private static final Logger LOGGER = LoggerFactory.getLogger(ControlledListProcessor.class);

    private final JpaRepositoryConfiguration repositoryConfiguration;

    public ControlledListProcessor(final JpaRepositoryConfiguration repositoryConfiguration) {
        this.repositoryConfiguration = repositoryConfiguration;
    }

    /**
     * Return metadata about controlled lists
     * @return
     */
    public Map<String, ControlledListsDto> getListMetaData() {
        Map<String, ControlledListsDto> result = new HashMap<>();
        for (ControlledListsList list : ControlledListsList.values()) {
            result.put(list.getPath(),
                    new ControlledListsDto(list.getDescription(), list.getPath(), list.getDisplayHeaders(), list.getSearchableFields()));
        }
        return result;
    }

    /**
     * Return controlled list data for a given list.
     * @param controlledList The controlled list for which to return data
     * @return
     */
    private Pair<String, List<? extends MasterDataEntity>> getListData(ControlledListsList controlledList) {
        BaseRepository<? extends MasterDataEntity, ? extends Serializable> repository = repositoryConfiguration
                .getRepository(controlledList.getEntityClass());
        return new ImmutablePair<>(controlledList.getPath(), sortedList(repository.findAll()));
    }

    /**
     * Search by an arbituary field containing string
     * @param controlledList The controlled list to return
     * @param contains The search term
     * @return The filtered list
     */
    public Pair<String, List<? extends MasterDataEntity>> getListData(ControlledListsList controlledList, String contains) {
        if (StringUtils.isNotEmpty(contains)) {
            final List<String> terms = Arrays.asList(contains.split("\\s+"));
            BaseRepository<? extends MasterDataEntity, ? extends Serializable> repository = repositoryConfiguration
                    .getRepository(controlledList.getEntityClass());

            BooleanExpression searchPredicate = null;
            for (StringPath path : controlledList.getSearchablePaths()) {
                for (String term : terms) {
                    if (searchPredicate == null) {
                        searchPredicate = path.containsIgnoreCase(term);
                    } else {
                        searchPredicate = searchPredicate.or(path.containsIgnoreCase(term));
                    }
                }
            }
            List<? extends MasterDataEntity> entities = IterableUtils.toList(repository.findAll(searchPredicate));
            return new ImmutablePair<>(controlledList.getPath(), sortedList(entities));
        }
        return getListData(controlledList);
    }

    /**
     * Returns the data at the level in a hierarchy
     * @param hierarchy The hierarchy to interrogate
     * @param entities The list of entities implying the route to the require level
     * @param field
     *@param contains @return A pair of the controlled list path and the list
     */
    public Pair<String, List<? extends MasterDataEntity>> getListData(Hierarchy hierarchy, Set<MasterDataEntity> entities,
            String field, String contains) {
        Pair<HierarchyLevel, List<? extends MasterDataEntity>> results = null;
        if (field == null || field.isEmpty() || contains == null || contains.isEmpty()) {
            results = hierarchy.children(entities);
        } else {
            results = hierarchy.children(entities, field, contains);
        }
        return new ImmutablePair<>(results.getLeft().getControlledList().getPath(), results.getRight());
    }

    /**
     * return the validation for a group of entities against a hierarchy
     * @param hierarchy The hierarchy to interrogate
     * @param entities The list of entities implying the route to the require level
     */
    public Pair<String, Hierarchy.Result> validate(Hierarchy hierarchy, Set<MasterDataEntity> entities) {
        Pair<HierarchyLevel, Hierarchy.Result> result = hierarchy.validate(entities);
        return new ImmutablePair<>(result.getLeft().getControlledList().getPath(), result.getRight());
    }

    /**
     * Convenience method to sort list entries in the natural order by primary key
     *
     * @param list the list of {@link MasterDataEntity} to be sorted
     * @param <E>
     * @return the sorted list (in place)
     */
    private static <E extends MasterDataEntity> List<E> sortedList(Collection<E> list) {
        return list.stream().filter(E::isPrimary).sorted(Comparator.comparing(E::getName)).collect(Collectors.toList());
    }
}