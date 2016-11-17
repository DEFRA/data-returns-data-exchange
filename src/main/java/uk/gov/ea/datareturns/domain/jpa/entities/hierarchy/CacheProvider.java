package uk.gov.ea.datareturns.domain.jpa.entities.hierarchy;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import uk.gov.ea.datareturns.domain.jpa.dao.DependenciesDao;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

/**
 * The provider of the validation rules cache
 */
public abstract class CacheProvider<T> {
     protected static final Logger LOGGER = LoggerFactory.getLogger(DependenciesDao.class);
     @PersistenceContext
     protected EntityManager entityManager;

     /*
           * Detect the exclusion^ character at the beginning of a string
           */
     public static boolean IsExclusion(String s) {
         return s.charAt(0) == '^' ? true : false;
     }

     /*
           * Return a string with any exclusion characters removed
           * or return the unmutated string
           */
     public static String removeExclusion(String s) {
         return !IsExclusion(s) ? s : s.substring(1);
     }

     public abstract T getCache();
}
