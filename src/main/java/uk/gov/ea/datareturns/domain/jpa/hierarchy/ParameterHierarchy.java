package uk.gov.ea.datareturns.domain.jpa.entities.hierarchy;

import org.springframework.stereotype.Component;
import uk.gov.ea.datareturns.domain.jpa.dao.DependenciesDao;
import uk.gov.ea.datareturns.domain.jpa.entities.Parameter;
import uk.gov.ea.datareturns.domain.jpa.entities.ReleasesAndTransfers;
import uk.gov.ea.datareturns.domain.jpa.entities.ReturnType;
import uk.gov.ea.datareturns.domain.jpa.entities.Unit;

import javax.inject.Inject;

/**
 * Created by graham on 16/11/16.
 */
@Component
public class ParameterHierarchy extends Hierarchy {

    @Inject
    public ParameterHierarchy(DependenciesDao dependenciesDao) {
        super(new HierarchyNode[] {
                        new HierarchyNode(ReturnType.class),
                        new HierarchyNode(ReleasesAndTransfers.class),
                        new HierarchyNode(Parameter.class),
                        new HierarchyNode(Unit.class)
                }, dependenciesDao);
    }

}

