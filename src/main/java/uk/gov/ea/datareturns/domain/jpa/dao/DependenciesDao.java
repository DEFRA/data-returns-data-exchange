package uk.gov.ea.datareturns.domain.jpa.dao;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Repository;
import uk.gov.ea.datareturns.domain.exceptions.ProcessingException;
import uk.gov.ea.datareturns.domain.jpa.entities.*;

import javax.annotation.PostConstruct;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Created by graham on 03/10/16.
 */
@Repository
public class DependenciesDao {
    protected static final Logger LOGGER = LoggerFactory.getLogger(DependenciesDao.class);

    @PersistenceContext
    protected EntityManager entityManager;

    @Inject
    private ParameterDao parameterDao;

    @Inject
    private ReturnTypeDao returnTypeDao;

    @Inject
    private ReleasesAndTransfersDao releasesAndTransfersDao;

    @Inject
    private UnitDao unitDao;

    public Dependencies getById(DependenciesId id) {
        return entityManager.find(Dependencies.class, id);
    }

    //HashMap<ReturnType, HashMap<ReleasesAndTransfers, HashMap<Parameters, HashSet<Unit>>>>;
    Map<String, Map<String, Map<String, Set<String>>>> cache = null;

    private Comparator<Dependencies> groupByComparator = Comparator
            .comparing(Dependencies::getReturnType)
            .thenComparing(Dependencies::getReleasesAndTransfers)
            .thenComparing(Dependencies::getParameter)
            .thenComparing(Dependencies::getUnits);

    public Map<String, Map<String, Map<String, Set<String>>>> buildCache() {
        if (cache == null) {
            LOGGER.info("Build name cache of: Dependencies");
            cache = list().stream().collect(
                Collectors.groupingBy(t -> returnTypeDao.getKeyFromRelaxedName(t.getReturnType()),
                    Collectors.groupingBy(r -> releasesAndTransfersDao.getKeyFromRelaxedName(r.getReleasesAndTransfers()),
                        Collectors.groupingBy(p -> parameterDao.getKeyFromRelaxedName(p.getParameter()),
                            Collectors.mapping(u -> unitDao.getKeyFromRelaxedName(u.getUnits()), Collectors.toCollection(HashSet::new))
                        )
                    )
                )
            );
        }

        return cache;
    }

    public Map<String, Map<String, Map<String, Set<String>>>> getCache() {
        return buildCache();
    }

    /**
     * List all the dependencies
     */
    public List<Dependencies> list() {
        CriteriaBuilder cb = entityManager.getCriteriaBuilder();
        CriteriaQuery<Dependencies> q = cb.createQuery(Dependencies.class);
        Root<Dependencies> c = q.from(Dependencies.class);
        q.select(c);
        TypedQuery<Dependencies> query = entityManager.createQuery(q);
        List<Dependencies> results = query.getResultList();
        results.sort(groupByComparator);
        return results;
    }

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

    /**
     * Test that all items in the dependencies table
     * can be found in the base tables
     * @return true is OK
     */
    @PostConstruct
    public void checkIntegrity() throws ProcessingException{
        boolean hasIntegrity = true;

        // Test parameters
        List<String> missingParameters = list()
            .stream()
                .map(Dependencies::getParameter)
                .map(p -> removeExclusion(p))
                .filter(p -> !p.trim().equals("*"))
                .filter(p -> !p.trim().equals("^*"))
                .filter(p -> !p.trim().equals("*-"))
                .filter(p -> !p.trim().equals("..."))
                .filter(p -> !parameterDao.nameExists(p))
                .collect(Collectors.toList());

        if (missingParameters.size() != 0) {
            hasIntegrity = false;
            for(String name : missingParameters) {
                LOGGER.error("Dependent parameter not found: " + name);
            }
        }

        // Test return types
        List<String> missingReturnTypes = list()
                .stream()
                .map(Dependencies::getReturnType)
                .map(p -> removeExclusion(p))
                .filter(p -> !p.trim().equals("*"))
                .filter(p -> !p.trim().equals("^*"))
                .filter(p -> !p.trim().equals("*-"))
                .filter(p -> !p.trim().equals("..."))
                .filter(p -> !returnTypeDao.nameExists(p))
                .collect(Collectors.toList());

        if (missingReturnTypes.size() != 0) {
            hasIntegrity = false;
            for(String name : missingReturnTypes) {
                LOGGER.error("Dependent return type not found: " + name);
            }
        }

        // Test releases and transfers
        List<String> missingReleasesAndTransfers = list()
                .stream()
                .map(Dependencies::getReleasesAndTransfers)
                .map(p -> removeExclusion(p))
                .filter(p -> !p.trim().equals("*"))
                .filter(p -> !p.trim().equals("^*"))
                .filter(p -> !p.trim().equals("*-"))
                .filter(p -> !p.trim().equals("..."))
                .filter(p -> !releasesAndTransfersDao.nameExists(p))
                .collect(Collectors.toList());

        if (missingReleasesAndTransfers.size() != 0) {
            hasIntegrity = false;
            for(String name : missingReleasesAndTransfers) {
                LOGGER.error("Dependent releases and transfer type not found: " + name);
            }
        }

        // Test units
        List<String> missingUnits = list()
                .stream()
                .map(Dependencies::getUnits)
                .map(p -> removeExclusion(p))
                .filter(p -> !p.trim().equals("*"))
                .filter(p -> !p.trim().equals("^*"))
                .filter(p -> !p.trim().equals("*-"))
                .filter(p -> !p.trim().equals("..."))
                .filter(p -> !unitDao.nameExists(p))
                .collect(Collectors.toList());

        if (missingUnits.size() != 0) {
            hasIntegrity = false;
            for(String name : missingUnits) {
                LOGGER.error("Dependent units not found: " + name);
            }
        }

        if (!hasIntegrity) {
            throw new ProcessingException("Dependencies data has errors");
        }
    }
}
