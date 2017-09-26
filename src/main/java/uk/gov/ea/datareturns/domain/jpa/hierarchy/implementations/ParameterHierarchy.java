package uk.gov.ea.datareturns.domain.jpa.hierarchy.implementations;

import org.springframework.stereotype.Component;
import uk.gov.ea.datareturns.domain.jpa.dao.masterdata.ParameterHierarchyDao;
import uk.gov.ea.datareturns.domain.jpa.entities.masterdata.MasterDataEntity;
import uk.gov.ea.datareturns.domain.jpa.entities.masterdata.impl.*;
import uk.gov.ea.datareturns.domain.jpa.hierarchy.Hierarchy;
import uk.gov.ea.datareturns.domain.jpa.hierarchy.HierarchyGroupLevel;
import uk.gov.ea.datareturns.domain.jpa.hierarchy.HierarchyLevel;
import uk.gov.ea.datareturns.domain.jpa.hierarchy.processors.GroupNavigator;
import uk.gov.ea.datareturns.domain.jpa.hierarchy.processors.GroupValidator;
import uk.gov.ea.datareturns.domain.jpa.service.MasterDataLookupService;
import uk.gov.ea.datareturns.domain.jpa.service.MasterDataNaturalKeyService;

import javax.inject.Inject;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.Set;

/**
 * Implementation of the parameter hierarchy.
 * The hierarchy levels are added to the hierarchy to define they structure of the hierarchy
 *
 * Please be aware the name of the component "parameter-hierarchy" is used in the Restful service so that
 * the endpoint can act on any hierarchy. See ControlledListResource.getHierarchyLevel
 *
 * @author Graham Willis
 */
@Component("parameter-hierarchy")
public class ParameterHierarchy extends Hierarchy<ParameterHierarchyDao> {
    private static final Set<HierarchyLevel<? extends MasterDataEntity>> hierarchy = new LinkedHashSet<>();

    static {
        hierarchy.add(new HierarchyGroupLevel<>(ReturnType.class, ControlledListsList.RETURN_TYPE, "sector"));
        hierarchy.add(new HierarchyLevel<>(ReleasesAndTransfers.class, ControlledListsList.RELEASES_AND_TRANSFER));
        hierarchy.add(new HierarchyLevel<>(Parameter.class, ControlledListsList.PARAMETER));
        hierarchy.add(new HierarchyGroupLevel<>(Unit.class, ControlledListsList.UNIT, "type"));
    }

    @Inject
    public ParameterHierarchy(MasterDataLookupService lookupService, MasterDataNaturalKeyService keyService,
            ParameterHierarchyDao parameterHierarchyDao, GroupNavigator hierarchyNavigator, GroupValidator hierarchyValidator) {
        super(lookupService, keyService, Collections.unmodifiableSet(hierarchy), parameterHierarchyDao, hierarchyNavigator,
                hierarchyValidator);
    }
}

