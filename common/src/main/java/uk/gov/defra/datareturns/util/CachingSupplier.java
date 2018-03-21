package uk.gov.defra.datareturns.util;

import java.util.function.Supplier;

/**
 * Implementation of the {@link Supplier} interface to provide lazy instantiation and caching facilities.
 * <p>
 * Uses double-checked locking to provide lazy instantiation of the data to be cached.
 * <p>
 * Examples:
 * <code>
 * public class MyClass {
 * private CachingSupplier<Map<String, String>> cache = CachingSupplier.of(() -> {
 * Map<String, String> data = expensiveOperationToBuildCache();
 * return data;
 * });
 * // or better still...
 * private CachingSupplier<Map<String, String>> cache = CachingSupplier.of(this::expensiveOperationToBuildCache);
 * <p>
 * ...
 * <p>
 * public String getValue(String key) {
 * // expensiveOperationToBuildCache() will be called now if it needs to be
 * return cache.get().get(key);
 * }
 * }
 * </code>
 *
 * @param <T> the type of data to be cached
 * @author Sam Gardner-Dell
 */
public interface CachingSupplier<T> extends Supplier<T> {

    /**
     * Wrap a delegate {@link Supplier} with lazy initialisation and caching capabilities.
     *
     * @param delegate the delgate {@link Supplier} (usually a reference to an expensive operation whose result we wish to cache)
     * @param <T>      the type of the data being cached
     * @return a {@link CachingSupplier} wrapping the delegate.
     */
    static <T> CachingSupplier<T> of(final Supplier<T> delegate) {
        return (delegate instanceof CachingSupplier) ? (CachingSupplier<T>) delegate : new CachingSupplierImpl<>(delegate);
    }

    /**
     * Retrieve the cached data and if necessary, builds the cache
     *
     * @return the data being cached.
     */
    @Override
    T get();

    /**
     * Clear the cache
     */
    void clear();

    /**
     * Reload the cache
     *
     * @return the new cache data,.
     */
    T reload();

    /**
     * Implementation of the {@link CachingSupplier} interface.
     *
     * @param <T>
     */
    class CachingSupplierImpl<T> implements CachingSupplier<T> {
        private final Supplier<T> delegate;
        private volatile T cache;

        // Private constructor
        private CachingSupplierImpl(final Supplier<T> delegate) {
            this.delegate = delegate;
        }

        /**
         * Retrieve the cached data and if necessary, builds the cache
         *
         * @return the data being cached.
         */
        @Override
        public T get() {
            // Local reference to reduce volatile reads
            T local = cache;
            if (local == null) {
                synchronized (this) {
                    local = cache;
                    if (local == null) {
                        local = delegate.get();
                        cache = local;
                    }
                }
            }
            return local;
        }

        /**
         * Clear the cache
         */
        public void clear() {
            if (cache != null) {
                synchronized (this) {
                    cache = null;
                }
            }
        }

        /**
         * Reload the cache
         *
         * @return the new cache data,.
         */
        public T reload() {
            clear();
            return get();
        }
    }
}
