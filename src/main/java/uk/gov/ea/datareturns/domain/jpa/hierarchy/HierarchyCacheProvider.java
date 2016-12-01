package uk.gov.ea.datareturns.domain.jpa.hierarchy;

import java.util.Map;

/**
 * @author Graham Willis
 * The provider of the validation rules cache. The cache must be of the form of nested maps
 * terminated by a set such as Map<String, Map<String, ....Set<String>...>
 */
public abstract class HierarchyCacheProvider<T extends Map<String, ?>> {
     public abstract T getCache();
}
