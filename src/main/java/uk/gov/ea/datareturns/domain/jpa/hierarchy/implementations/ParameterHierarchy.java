package uk.gov.ea.datareturns.domain.jpa.hierarchy.implementations;

import org.springframework.stereotype.Component;
import uk.gov.ea.datareturns.domain.jpa.dao.*;
import uk.gov.ea.datareturns.domain.jpa.entities.*;
import uk.gov.ea.datareturns.domain.jpa.hierarchy.Hierarchy;
import uk.gov.ea.datareturns.domain.jpa.hierarchy.HierarchyGroupLevel;
import uk.gov.ea.datareturns.domain.jpa.hierarchy.HierarchyLevel;
import uk.gov.ea.datareturns.domain.jpa.hierarchy.processors.GroupNavigator;
import uk.gov.ea.datareturns.domain.jpa.hierarchy.processors.GroupValidator;

import javax.inject.Inject;
import java.util.Collections;
import java.util.LinkedHashSet;

/**
 * Implementation of the parameter hierarchy.
 * The hierarchy levels are added to the hierarchy to define they structure of the hierarchy
 *
 * Please be aware the name of the component "parameter-hierarchy" is used in the Restful service so that
 * the endpoint can act on any hierarchy. See ControlledListResource.getHierarchyLevel
 *
 * @Author Graham Willis
 */
@Component("parameter-hierarchy")
public class ParameterHierarchy extends Hierarchy {

    @Inject
    public ParameterHierarchy(ParameterHierarchyDao parameterHierarchyDao, GroupNavigator hierarchyNavigator, GroupValidator hierarchyValidator) {
        super(Collections.unmodifiableSet(new LinkedHashSet() {{
            add(new HierarchyLevel<>(ReturnType.class, ReturnTypeDao.class, ControlledListsList.RETURN_TYPE));
            add(new HierarchyLevel<>(ReleasesAndTransfers.class, ReleasesAndTransfersDao.class, ControlledListsList.RELEASES_AND_TRANSFER));
            add(new HierarchyLevel<>(Parameter.class, ParameterDao.class, ControlledListsList.PARAMETER));
            add(new HierarchyGroupLevel<>(Unit.class, UnitDao.class, ControlledListsList.UNIT));
        }}), parameterHierarchyDao, hierarchyNavigator, hierarchyValidator);
    }
}

