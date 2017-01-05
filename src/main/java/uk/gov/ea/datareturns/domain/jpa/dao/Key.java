package uk.gov.ea.datareturns.domain.jpa.dao;

/**
 * Used to specify the type of lookup required by a dao
 *
 * @author Sam Gardner-Dell
 */
public interface Key {
    /**
     * Create a new key to request an explicit lookup for entities matching the given term.
     * An explicit lookup requires an exact match (including spacing).
     *
     * @param lookup the term used to perform the lookup
     * @return an explicit key for the given term
     */
    static Key explicit(String lookup) {
        return new KeyImpl(lookup).explicit();
    }

    /**
     * Create a new key to request a relaxed lookup for entities matching the given term.
     * A relaxed lookup is DAO specific in nature but usually ignores whitespace and capitalisation to find a match.
     *
     * @param lookup the term used to perform the lookup
     * @return an relaxed key for the given term
     */
    static Key relaxed(String lookup) {
        return new KeyImpl(lookup).relaxed();
    }

    /**
     * Sets this key to use explicit lookup
     *
     * @return a reference to the key
     */
    Key explicit();

    /**
     * Sets this key to use relaxed lookup
     *
     * @return a reference to the key
     */
    Key relaxed();

    /**
     * Retrieve the term to look up
     *
     * @return the lookup term
     */
    String getLookup();

    /**
     * @return true if this key requires an explicit lookup, false otherwise
     */
    boolean isExplicit();

    /**
     * Default key implementation
     */
    class KeyImpl implements Key {
        private final String lookup;
        private boolean explicit = true;

        /* Private constructor */
        private KeyImpl(String lookup) {
            this.lookup = lookup;
        }

        @Override public KeyImpl explicit() {
            this.explicit = true;
            return this;
        }

        @Override public KeyImpl relaxed() {
            this.explicit = false;
            return this;
        }
        @Override public boolean isExplicit() {
            return explicit;
        }
        @Override public String getLookup() {
            return lookup;
        }
    }
}