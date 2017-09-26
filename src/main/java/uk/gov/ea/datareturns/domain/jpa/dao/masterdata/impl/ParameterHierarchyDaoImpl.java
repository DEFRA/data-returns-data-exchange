package uk.gov.ea.datareturns.domain.jpa.dao.masterdata.impl;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Repository;
import uk.gov.ea.datareturns.domain.exceptions.ProcessingException;
import uk.gov.ea.datareturns.domain.jpa.dao.masterdata.ParameterHierarchyDao;
import uk.gov.ea.datareturns.domain.jpa.entities.masterdata.impl.*;
import uk.gov.ea.datareturns.domain.jpa.hierarchy.HierarchyGroupSymbols;
import uk.gov.ea.datareturns.domain.jpa.hierarchy.HierarchySymbols;
import uk.gov.ea.datareturns.domain.jpa.service.MasterDataLookupService;
import uk.gov.ea.datareturns.domain.jpa.service.MasterDataNaturalKeyService;
import uk.gov.ea.datareturns.util.CachingSupplier;

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
 * responsible for reading and caching the parameter hierarchy
 *
 * The parameter hierarchy cache takes the form:
 * Map<String1, Map<String2, Map<String3, Set<String4>>>>
 * where:
 *      String1 is the return type name
 *      String2 is the releases and transfers name
 *      String3 is the parameter name
 *      String4 is the unit or unit group name
 *
 * @author Graham Willis
 */
@Repository
public class ParameterHierarchyDaoImpl implements ParameterHierarchyDao {
    protected static final Logger LOGGER = LoggerFactory.getLogger(ParameterHierarchyDaoImpl.class);

    // FIXME: This cache strategy needs to be re-thought.
    private final CachingSupplier<Map<String, Map<String, Map<String, Set<String>>>>> cache = CachingSupplier.of(this::cacheSupplier);

    @PersistenceContext
    protected EntityManager entityManager;

    private final MasterDataNaturalKeyService keyService;
    private final MasterDataLookupService lookupService;

    @Inject
    public ParameterHierarchyDaoImpl(MasterDataNaturalKeyService keyService, MasterDataLookupService lookupService) {
        this.keyService = keyService;
        this.lookupService = lookupService;
    }

    @Override public ParameterHierarchy getById(ParameterHierarchyId id) {
        return entityManager.find(ParameterHierarchy.class, id);
    }

    @Override public Map<String, Map<String, Map<String, Set<String>>>> getCache() {
        return cache.get();
    }

    /*
     * Use to sort the results of the query, strictly unnecessary
     */
    private Comparator<ParameterHierarchy> groupByComparator = Comparator
            .comparing(ParameterHierarchy::getReturnType)
            .thenComparing(ParameterHierarchy::getReleasesAndTransfers)
            .thenComparing(ParameterHierarchy::getParameter)
            .thenComparing(ParameterHierarchy::getUnits);

    /**
     * Method to populate the dependencies cache.  This is invoked lazily when the cache needs to be built or is explicitly cleared/rebuilt.
     * @return a {@link Map} of cache data
     */
    private Map<String, Map<String, Map<String, Set<String>>>> cacheSupplier() {
        LOGGER.info("Build name cache of: ParameterHierarchy");
        return list().stream().collect(
                Collectors.groupingBy(t -> keyService.relaxKey(ReturnType.class, t.getReturnType()),
                        Collectors.groupingBy(r -> keyService.relaxKey(ReleasesAndTransfers.class, r.getReleasesAndTransfers()),
                                Collectors.groupingBy(p -> keyService.relaxKey(Parameter.class, p.getParameter()),
                                        Collectors.mapping(u -> keyService.relaxKey(Unit.class, u.getUnits()),
                                                Collectors.toCollection(HashSet::new))
                                )
                        )
                )
        );
    }

    /**
     * List all the dependencies
     */
    @Override public List<ParameterHierarchy> list() {
        CriteriaBuilder cb = entityManager.getCriteriaBuilder();
        CriteriaQuery<ParameterHierarchy> q = cb.createQuery(ParameterHierarchy.class);
        Root<ParameterHierarchy> c = q.from(ParameterHierarchy.class);
        q.select(c);
        TypedQuery<ParameterHierarchy> query = entityManager.createQuery(q);
        List<ParameterHierarchy> results = query.getResultList();
        results.sort(groupByComparator);
        return results;
    }

    /**
     * Test that all items in the dependencies table
     * can be found in the base tables
     * @return true is OK
     */
    @Override @PostConstruct
    public void checkIntegrity() throws ProcessingException {
        boolean hasIntegrity = true;

        // Test parameters
        List<String> missingParameters = list()
                .stream()
                .map(ParameterHierarchy::getParameter)
                .map(HierarchySymbols::removeExclusion)
                .filter(p -> !StringUtils.equalsAny(p.trim(), HierarchySymbols.ALL_SYMBOLS))
                .filter(p -> !HierarchyGroupSymbols.isGroup(p))
                .filter(p -> !(lookupService.relaxed().exists(Parameter.class, p)))
                .collect(Collectors.toList());

        if (missingParameters.size() != 0) {
            hasIntegrity = false;
            for (String name : missingParameters) {
                LOGGER.error("Dependent parameter not found: " + name);
            }
        }

        // Test return types
        List<String> missingReturnTypes = list()
                .stream()
                .map(ParameterHierarchy::getReturnType)
                .map(HierarchySymbols::removeExclusion)
                .filter(p -> !StringUtils.equalsAny(p.trim(), HierarchySymbols.ALL_SYMBOLS))
                .filter(p -> !HierarchyGroupSymbols.isGroup(p))
                .filter(p -> !lookupService.relaxed().exists(ReturnType.class, p))
                .collect(Collectors.toList());

        if (missingReturnTypes.size() != 0) {
            hasIntegrity = false;
            for (String name : missingReturnTypes) {
                LOGGER.error("Dependent return type not found: " + name);
            }
        }

        // Test releases and transfers
        List<String> missingReleasesAndTransfers = list()
                .stream()
                .map(ParameterHierarchy::getReleasesAndTransfers)
                .map(HierarchySymbols::removeExclusion)
                .filter(p -> !StringUtils.equalsAny(p.trim(), HierarchySymbols.ALL_SYMBOLS))
                .filter(p -> !HierarchyGroupSymbols.isGroup(p))
                .filter(p -> !lookupService.relaxed().exists(ReleasesAndTransfers.class, p))
                .collect(Collectors.toList());

        if (missingReleasesAndTransfers.size() != 0) {
            hasIntegrity = false;
            for (String name : missingReleasesAndTransfers) {
                LOGGER.error("Dependent releases and transfer type not found: " + name);
            }
        }

        // Test units
        List<String> missingUnits = list()
                .stream()
                .map(ParameterHierarchy::getUnits)
                .map(HierarchySymbols::removeExclusion)
                .filter(p -> !StringUtils.equalsAny(p.trim(), HierarchySymbols.ALL_SYMBOLS))
                .filter(p -> !HierarchyGroupSymbols.isGroup(p))
                .filter(p -> !lookupService.relaxed().exists(Unit.class, p))
                .collect(Collectors.toList());

        if (missingUnits.size() != 0) {
            hasIntegrity = false;
            for (String name : missingUnits) {
                LOGGER.error("Dependent units not found: " + name);
            }
        }

        if (!hasIntegrity) {
            throw new ProcessingException("Parameter Hierarchy data has errors");
        }
    }
}
