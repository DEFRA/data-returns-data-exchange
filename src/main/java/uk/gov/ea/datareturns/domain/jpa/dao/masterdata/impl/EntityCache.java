package uk.gov.ea.datareturns.domain.jpa.dao.masterdata.impl;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.Supplier;
import java.util.stream.Collectors;

/**
 * Provides a cache with multiple views
 *
 * @param <K>  the cache key type
 * @param <E>  the cache entity type
 *
 * @author Sam Gardner-Dell
 */
public interface EntityCache<K, E> {
    /**
     * Retrieve the cache data for the default view
     *
     *  @return the cache data for the default view as a {@link Map}
     */
    Map<K, E> defaultView();

    /**
     * Retrieve the cache data for a named view
     * @param viewName the view name
     *  @return the cache data for the named view as a {@link Map}
     */
    Map<K, E> forView(String viewName);

    /**
     * Create a new {@link EntityCache} for the specified data and with the specified views
     *
     * @param cacheData a {@link Collection} of entities providing the data behind the views
     * @param defaultView the specification for the default view
     * @param views the views  any number of additional views
     * @return the entity cache
     */
    @SafeVarargs
    static <K, E> EntityCache<K, E> build(Collection<E> cacheData, View<K, E> defaultView, View<K, E>... views) {
        return EntityCache.build(() -> cacheData, defaultView, views);
    }

    /**
     * Create a new {@link EntityCache} for the specified data and with the specified views
     *
     * @param cacheData a {@link Supplier} providing a {@link Collection} of entities providing the data behind the views
     * @param defaultView the specification for the default view
     * @param views the views  any number of additional views
     * @return the entity cache
     */
    @SafeVarargs
    static <K, E> EntityCache<K, E> build(Supplier<Collection<E>> cacheData, View<K, E> defaultView, View<K, E>... views) {
        return new EntityCacheImpl<>(cacheData, defaultView, views);
    }

    /**
     * Default implementation of {@link EntityCache}
     * @author Sam Gardner-Dell
     */
    class EntityCacheImpl<K, E> implements EntityCache<K, E> {
        /** the cache data */
        final Collection<E> cacheData;
        /** the default view cache */
        final Map<K, E> defaultViewCache;
        /** map of views by name (key) to a Map of cache data (value) */
        final Map<String, Map<K, E>> cacheByView;


        @SafeVarargs
        private EntityCacheImpl(Supplier<Collection<E>> cache, View<K, E> defaultView, View<K, E>... views) {
            this.cacheData = cache.get();
            this.defaultViewCache = this.cacheData.stream()
                    .filter(defaultView.predicate())
                    .collect(Collectors.toMap(defaultView.keyMapper(), e -> e));

            this.cacheByView = new HashMap<>();
            for (View<K, E> view : views) {
                cacheByView.put(view.name(),
                        this.cacheData.stream()
                                .filter(view.predicate())
                                .collect(Collectors.toMap(view.keyMapper(), e -> e)));
            }
        }

        @Override public Map<K, E> defaultView() {
            return this.defaultViewCache;
        }

        @Override public Map<K, E> forView(String viewName) {
            return this.cacheByView.get(viewName);
        }
    }

    /**
     * The interface View.
     *
     * @param <K>  the type of the key for this view
     * @param <E>  the type of the entity for this view
     * @author Sam Gardner-Dell
     */
    interface View<K, E> {
        /**
         * @return the name for this view
         */
        String name();

        /**
         * @return the predicate used to determine which entities are included in this view
         */
        Predicate<E> predicate();

        /**
         * @return the key mapper function used to generate keys for entities within the view
         */
        Function<E, K> keyMapper();

        /**
         * Create a new {@link View} for the given name
         *
         * This method does not filter the data behind this {@link View}
         *
         * @param <K>  the type of the key for this view
         * @param <E>  the type of the entity for this view
         * @param name the name of the view
         * @param keyMapper the key mapper function used to build the {@link Map} key for each entity
         * @return a new {@link View} for the given specification
         */
        static <K, E> View<K, E> of(String name, Function<E, K> keyMapper) {
            return new ViewImpl<>(name, keyMapper, e -> true);
        }

        /**
         * Create a new {@link View} for the given name
         *
         * This method does not filter the data behind this {@link View}
         * @param <K>  the type of the key for this view
         * @param <E>  the type of the entity for this view
         * @param name the name of the view
         * @param keyMapper the key mapper function used to build the {@link Map} key for each entity
         * @param predicate the predicate the {@link Predicate} function used to filter the data to generate the view
         * @return a new {@link View} for the given specification
         */
        static <K, E> View<K, E> of(String name, Function<E, K> keyMapper, Predicate<E> predicate) {
            return new ViewImpl<>(name, keyMapper, predicate);
        }
    }

    /**
     * Default {@link View} implementation
     */
    class ViewImpl<K, E> implements View<K, E> {
        private final String name;
        private final Function<E, K> keyMapper;
        private final Predicate<E> predicate;

        /* Private constructor */
        private ViewImpl(String name, Function<E, K> keyMapper, Predicate<E> predicate) {
            this.name = name;
            this.keyMapper = keyMapper;
            this.predicate = predicate;
        }

        @Override public String name() {
            return this.name;
        }

        @Override public Predicate<E> predicate() {
            return this.predicate;
        }

        @Override public Function<E, K> keyMapper() {
            return this.keyMapper;
        }
    }
}