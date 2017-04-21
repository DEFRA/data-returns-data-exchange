package uk.gov.ea.datareturns.domain.jpa.dao.masterdata.impl;

import org.apache.commons.collections4.BidiMap;
import org.apache.commons.collections4.bidimap.DualHashBidiMap;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.transaction.annotation.Transactional;
import uk.gov.ea.datareturns.domain.jpa.dao.masterdata.EntityDao;
import uk.gov.ea.datareturns.domain.jpa.dao.masterdata.Key;
import uk.gov.ea.datareturns.domain.jpa.dao.masterdata.SearchFunction;
import uk.gov.ea.datareturns.domain.jpa.dao.masterdata.AliasingEntityDao;
import uk.gov.ea.datareturns.domain.jpa.entities.masterdata.ControlledListEntity;
import uk.gov.ea.datareturns.domain.jpa.hierarchy.Hierarchy;
import uk.gov.ea.datareturns.domain.jpa.hierarchy.processors.GroupingEntityCommon;
import uk.gov.ea.datareturns.util.CachingSupplier;
import uk.gov.ea.datareturns.util.TextUtils;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
import java.beans.BeanInfo;
import java.beans.IntrospectionException;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.*;
import java.util.function.Predicate;
import java.util.stream.Collectors;

/**
 * Base class for JPA based DAO classes
 *
 * @author Graham Willis
 */
public abstract class AbstractEntityDao<E extends ControlledListEntity> implements EntityDao<E> {
    protected static final Logger LOGGER = LoggerFactory.getLogger(AbstractEntityDao.class);
    private final GroupingEntityCommon<? extends Hierarchy.GroupedHierarchyEntity> groupingEntityCommon;

    private final CachingSupplier<EntityCache<String, E>> cache = CachingSupplier.of(this::cacheBuilder);
    protected final String CACHE_ALL_ENTITIES = "CACHE_ALL_ENTITIES";

    private final CachingSupplier<BidiMap<String, String>> mashAndNameCache = CachingSupplier.of(this::cacheMashToNameSupplier);

    private final Map<String, SearchFunction<E>> searchMappings = new LinkedHashMap<>();

    private final Class<E> entityClass;
    private BeanInfo entityBeanInfo;

    private final ApplicationEventPublisher publisher;

    @PersistenceContext
    protected EntityManager entityManager;

    /**
     * Let the Dao class know the type of entity in order that type-safe
     * hibernate operations can be performed
     */
    public AbstractEntityDao(Class<E> entityClass, ApplicationEventPublisher publisher) {
        this(entityClass, publisher, null);
    }

    /**
     * For entities in the hierarchy that require grouping functions a GroupingEntityCommon is used.
     * The principle applied here is composition over inheritance.
     * @param entityClass
     * @param groupingEntityCommon
     */
    public AbstractEntityDao(Class<E> entityClass, ApplicationEventPublisher publisher,
                             GroupingEntityCommon<? extends Hierarchy.GroupedHierarchyEntity> groupingEntityCommon
                             ) {

        this.entityClass = entityClass;

        try {
            this.entityBeanInfo = Introspector.getBeanInfo(entityClass);
        } catch (IntrospectionException e) {
            LOGGER.error("Unable to retrieve entity bean information", e);
        }

        addSearchField("name",
                (entity, terms) -> terms.stream().anyMatch((term) -> StringUtils.containsIgnoreCase(entity.getName(), term)));

        this.groupingEntityCommon = groupingEntityCommon;
        if (this.groupingEntityCommon != null) {
            this.groupingEntityCommon.setDao(this);
        }

        this.publisher = publisher;

    }

    @Override public GroupingEntityCommon<? extends Hierarchy.GroupedHierarchyEntity> getGroupingEntityCommon() {
        return groupingEntityCommon;
    }

    /**
     * Get an entity of type <E> by its primary key
     *
     * @param id The entity identifier
     * @return E
     */
    @Override public E getById(final long id) {
        return entityManager.find(entityClass, id);
    }

    @Override public E getByName(String name) {
        return getByName(Key.explicit(name));
    }

    @Override public E getByName(Key name) {
        return getCache().defaultView().get(usingKey(name));
    }

    @Override public boolean nameExists(final Key name) {
        return getByName(name) != null;
    }

    protected String usingKey(Key key) {
        return key.isExplicit() ? key.getLookup() : lookupNameFromMash(generateMash(key.getLookup()));
    }

    /**
     * List all entities of type E.
     *
     * @return List<E>
     */
    @Override public List<E> list() {
        return new ArrayList<>(getCache().defaultView().values());
    }

    /**
     * List all entities of type <E> which satisfy a predicate
     *
     * @param predicate The predicate
     * @return List<E>
     */
    @Override public List<E> list(Predicate<E> predicate) {
        return getCache().defaultView().values().stream().filter(predicate).collect(Collectors.toList());
    }

    /**
     * List the entities of type <E> filtered such that the field contains the search term
     *
     * @param field The name of the field used for filtering
     * @param contains The search term
     * @return A filtered list
     */
    @Override public List<E> list(String field, String contains) {
        if (entityBeanInfo != null) {
            for (PropertyDescriptor pd : entityBeanInfo.getPropertyDescriptors()) {
                if (pd.getReadMethod() != null && pd.getName().toLowerCase().equals(field.toLowerCase())) {
                    final Method readMethod = pd.getReadMethod();
                    Predicate<E> builtPredicate = e -> {
                        try {
                            return containsIgnoreCaseIgnoreSpaces(Objects.toString(readMethod.invoke(e)), contains);
                        } catch (IllegalAccessException | InvocationTargetException ex) {
                            return true;
                        }
                    };
                    return list(builtPredicate);
                }
            }
        }
        return list();
    }

    /**
     * Search for entities which match ANY of the given search terms.
     *
     * The nature of the search is specific to the entity.  All entities will search names but specific subclasses may customise the
     * implementation, for example to search aliases, or for parameters to search CAS numbers.
     *
     * @param terms the terms to search for
     * @return a {@link List} of matching entities.
     */
    @Override public final List<E> search(List<String> terms) {
        return list((item) -> searchMappings.values().stream().anyMatch((i) -> i.matches(item, terms)));
    }

    /**
     * Enable DAO implementations to add functionality to make individual entity fields searchable
     *
     * @param field the field identifier string
     * @param searchFunction the {@link SearchFunction} implementation to provide search functionality for a field
     */
    void addSearchField(String field, SearchFunction<E> searchFunction) {
        this.searchMappings.put(field, searchFunction);
    }

    /**
     * @return the set of field identifiers that are searchable for the entity <E>
     */
    public Set<String> getSearchFields() {
        return new LinkedHashSet<>(searchMappings.keySet());
    }

    /**
     * Retrieves a mashed version of the name from a cache.
     *
     * The name must exist in the cache or null will be returned.
     *
     * @param name the name by which to lookup a mash
     * @return the mashed version of the name if one is found in the cache, null otherwise
     */
    @Override public final String lookupMashFromName(String name) {
        return mashAndNameCache.get().getKey(name);
    }

    /**
     * Retrieves a name from the cache given an exact mashed value
     * The mash must exist in the cache or null will be returned.
     *
     * @param mash a mashed version of a name (space reduced/normalised according to entity specific rules)
     * @return the proper name for the given mash if one is found in the cache, null otherwise
     */
    @Override public final String lookupNameFromMash(final String mash) {
        return mashAndNameCache.get().get(mash);
    }

    /**
     * A method to convert the reduce variation in name in case and spacing
     * to a standard format which acts as the key. Here so it can be overridden. The default functionality
     * is to convert to upper cases and reduce multiple spaces to a single space to create the lookup key.
     *
     * Overriding implementations must ensure that generating a mash from a previously mashed value will produce the same result, such that:
     *     generateMash(value) == generateMash(generateMash(value)) holds true
     *
     * @param inputValue the entity name to be mashed
     * @return the mashed version of the name
     */
    public String generateMash(final String inputValue) {
        return inputValue == null ? null : TextUtils.normalize(inputValue.toUpperCase());
    }

    /**
     * Retrieve the {@link EntityCache}
     */
    protected final EntityCache<String, E> getCache() {
        return cache.get();
    }

    /**
     * Clear the caches
     */
    public final void clearCaches() {
        LOGGER.info("Clear caches for: " + entityClass.getSimpleName());
        cache.clear();
        mashAndNameCache.clear();
    }

    /**
     * Add a new entity of type <E>
     *
     */
    @Override @Transactional
    public final void add(E entity) {
        entityManager.persist(entity);
        LOGGER.info("Added: " + entityClass.getSimpleName() + "id: " + entity.getId() + " " + entity.getName());
    }

    /**
     * Remove an entity of type <E>
     *
     * @param id The entity identifier
     * @throws IllegalArgumentException
     */
    @Override @Transactional
    public final void removeById(long id) throws IllegalArgumentException {
        E entity = getById(id);
        entityManager.remove(entity);
        LOGGER.info("Deleted " + entityClass.getSimpleName() + ": " + id);
    }

    /**
     * Fetch all entities from the database
     *
     * Note: this method bypasses the cache and is primarily used to build cache views
     *
     * @return a {@link List} of all database entities returned via a query
     */
    protected final List<E> fetchAll() {
        CriteriaBuilder cb = entityManager.getCriteriaBuilder();
        CriteriaQuery<E> q = cb.createQuery(entityClass);
        Root<E> c = q.from(entityClass);
        q.select(c);
        TypedQuery<E> query = entityManager.createQuery(q);
        return query.getResultList();
    }

    /**
     * Get the class of the entity being operated on
     */
    @Override public Class<E> getEntityClass() {
        return entityClass;
    }

    /**
     * Helper function to compare two strings cases insensitively ignoring whitespace
     * @param a
     * @param b
     * @return
     */
    private static boolean containsIgnoreCaseIgnoreSpaces(String a, String b) {
        return StringUtils.containsIgnoreCase(
                TextUtils.normalize(a, TextUtils.WhitespaceHandling.REMOVE),
                TextUtils.normalize(b, TextUtils.WhitespaceHandling.REMOVE));
    }

    /**
     * Method to populate the bidirecitonal cache of mashes to names.
     * This is invoked lazily when the cache needs to be built or is explicitly cleared/rebuilt.
     * @return a {@link BidiMap} of mashes and names
     */
    private BidiMap<String, String> cacheMashToNameSupplier() {
        LOGGER.info("Build name/mash cache of: " + entityClass.getSimpleName());
        final List<E> data = fetchAll();
        final BidiMap<String, String> bidi = new DualHashBidiMap<>();
        for (E entry : data) {
            String name = entry.getName();
            String mash = generateMash(name);

            if (bidi.containsKey(mash) || bidi.containsValue(name)) {
                final String template = "Encountered a name/mash colision for two %s when building the cache.  "
                        + "Mash: '%s', Name: '%s'. The existing value will be replaced.";
                LOGGER.error(String.format(template, entityClass.getSimpleName(), mash, name));
            }
            bidi.put(mash, name);
        }
        return bidi;
    }

    /**
     * Method to populate the cache.  This is invoked lazily when the cache needs to be built or is explicitly cleared/rebuilt.
     *
     * This method can be overridden to allow:
     *  - the default cache view ({@link EntityCache#defaultView()} to be changed
     *  - additionak cache views to be created
     *  - implementation-specific initialisation of entities (e.g. populating aliases on each entity for subclasses of {@link AliasingEntityDao})
     *
     * Overriding implementations MUST NOT filter the cache in any way.
     *
     * @return a {@link Map} of cache data
     */
    protected EntityCache<String, E> cacheBuilder() {
        return EntityCache.build(fetchAll(), EntityCache.View.of(CACHE_ALL_ENTITIES, ControlledListEntity::getName));
    }
}