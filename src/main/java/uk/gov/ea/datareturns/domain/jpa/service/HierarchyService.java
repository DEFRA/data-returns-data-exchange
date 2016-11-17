package uk.gov.ea.datareturns.domain.jpa.service;

import org.springframework.stereotype.Component;
import uk.gov.ea.datareturns.domain.jpa.entities.Parameter;
import uk.gov.ea.datareturns.domain.jpa.entities.ReleasesAndTransfers;
import uk.gov.ea.datareturns.domain.jpa.entities.ReturnType;
import uk.gov.ea.datareturns.domain.jpa.entities.Unit;
import uk.gov.ea.datareturns.domain.jpa.entities.hierarchy.Hierarchy;
import uk.gov.ea.datareturns.domain.jpa.entities.hierarchy.ParameterHierarchy;

/**
 * A component to initialise and expose hierarchies
 */
@Component
public class HierarchyService {

    public static Hierarchy PARAMETER_HIERARCHY;

    static {
        PARAMETER_HIERARCHY = new ParameterHierarchy(new Hierarchy.HierarchyNode[] {
                PARAMETER_HIERARCHY.new HierarchyNode(ReturnType.class),
                PARAMETER_HIERARCHY.new HierarchyNode(ReleasesAndTransfers.class),
                PARAMETER_HIERARCHY.new HierarchyNode(Parameter.class),
                PARAMETER_HIERARCHY.new HierarchyNode(Unit.class)
        });
    }
}
