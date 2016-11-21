package uk.gov.ea.datareturns.domain.jpa.hierarchy;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import uk.gov.ea.datareturns.domain.jpa.dao.ParameterHierarchyDao;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

/**
 * @author Graham Willis
 * The provider of the validation rules cache. The cache must be of the form of nested maps
 * terminated by a set.
 */
public abstract class CacheProvider<T> {
     protected static final Logger LOGGER = LoggerFactory.getLogger(ParameterHierarchyDao.class);
     @PersistenceContext
     protected EntityManager entityManager;

     /*
      * Detect the exclusion^ character at the beginning of a string
      */
     public static boolean IsExclusion(String s) {
          return s.charAt(0) == HierarchySymbols.EXCLUDE.charAt(0) ? true : false;
     }

     /*
      * Return a string with any exclusion characters removed
      * or return the string
      */
     public static String removeExclusion(String s) {
          return !IsExclusion(s) ? s : s.substring(1);
     }

     public abstract T getCache();
}
