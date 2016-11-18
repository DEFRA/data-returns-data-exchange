package uk.gov.ea.datareturns.domain.jpa.hierarchy;

import org.springframework.stereotype.Component;
import uk.gov.ea.datareturns.domain.jpa.dao.*;
import uk.gov.ea.datareturns.domain.jpa.entities.*;

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
    public ParameterHierarchy(DependenciesDao dependenciesDao, HierarchyNavigator hierarchyNavigator, HierarchyValidator hierarchyValidator) {
        super(Collections.unmodifiableSet(new LinkedHashSet(){{
            add(new HierarchyNode<ReturnType>(ReturnType.class, ReturnTypeDao.class, ControlledListsList.RETURN_TYPE));
            add(new HierarchyNode(ReleasesAndTransfers.class, ReleasesAndTransfersDao.class, ControlledListsList.RELEASES_AND_TRANSFERS));
            add(new HierarchyNode(Parameter.class, ParameterDao.class, ControlledListsList.PARAMETERS));
            add(new HierarchyNode(Unit.class, UnitDao.class, ControlledListsList.UNITS));
        }}), dependenciesDao, hierarchyNavigator, hierarchyValidator);
    }
}

