package uk.gov.ea.datareturns.domain.jpa.hierarchy.implementations;

import org.springframework.stereotype.Component;
import uk.gov.ea.datareturns.domain.jpa.dao.*;
import uk.gov.ea.datareturns.domain.jpa.entities.*;
import uk.gov.ea.datareturns.domain.jpa.hierarchy.Hierarchy;
import uk.gov.ea.datareturns.domain.jpa.hierarchy.HierarchyLevel;

import javax.inject.Inject;
import java.util.Collections;
import java.util.LinkedHashSet;

/**
 * Implementation of the parameter hierarchy
 * @Author Graham Willis
 */
@Component
public class ParameterHierarchy extends Hierarchy {

    @Inject
    public ParameterHierarchy(ParameterHierarchyDao dependenciesDao, SimpleNavigator hierarchyNavigator, SimpleValidator hierarchyValidator) {
        super(Collections.unmodifiableSet(new LinkedHashSet(){{
            add(new HierarchyLevel<>(ReturnType.class, ReturnTypeDao.class, ControlledListsList.RETURN_TYPE));
            add(new HierarchyLevel<>(ReleasesAndTransfers.class, ReleasesAndTransfersDao.class, ControlledListsList.RELEASES_AND_TRANSFER));
            add(new HierarchyLevel<>(Parameter.class, ParameterDao.class, ControlledListsList.PARAMETER));
            add(new HierarchyLevel<>(Unit.class, UnitDao.class, ControlledListsList.UNIT));
        }}), dependenciesDao, hierarchyNavigator, hierarchyValidator);
    }
}

