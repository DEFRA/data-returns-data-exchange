package uk.gov.ea.datareturns.domain.jpa.dao.masterdata;

import uk.gov.ea.datareturns.domain.exceptions.ProcessingException;
import uk.gov.ea.datareturns.domain.jpa.entities.masterdata.impl.ParameterHierarchy;
import uk.gov.ea.datareturns.domain.jpa.entities.masterdata.impl.ParameterHierarchyId;
import uk.gov.ea.datareturns.domain.jpa.hierarchy.HierarchyCacheProvider;

import javax.annotation.PostConstruct;
import java.util.List;
import java.util.Map;
import java.util.Set;

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
public interface ParameterHierarchyDao extends HierarchyCacheProvider<Map<String, Map<String, Map<String, Set<String>>>>> {
    ParameterHierarchy getById(ParameterHierarchyId id);

    Map<String, Map<String, Map<String, Set<String>>>> getCache();

    /**
     * List all the dependencies
     */
    List<ParameterHierarchy> list();

    /**
     * Test that all items in the dependencies table
     * can be found in the base tables
     * @return true is OK
     */
    @PostConstruct void checkIntegrity() throws ProcessingException;
}
