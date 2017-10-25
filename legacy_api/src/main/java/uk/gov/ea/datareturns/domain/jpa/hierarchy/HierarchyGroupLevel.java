package uk.gov.ea.datareturns.domain.jpa.hierarchy;

import com.querydsl.core.types.dsl.StringPath;
import org.apache.commons.lang3.StringUtils;
import uk.gov.ea.datareturns.domain.jpa.entities.masterdata.MasterDataEntity;
import uk.gov.ea.datareturns.domain.jpa.entities.masterdata.impl.ControlledListsList;
import uk.gov.ea.datareturns.domain.jpa.repositories.EntityPathUtils;

import java.lang.reflect.Method;

/**
 * Represents a level in the hierarchy where grouping functions may be used. Groups are indicated by
 * their enclosure in square brackets []. If a group is indicated at a level in the hierarchy
 * then all entities what are members of the group will validate and will be listed
 * @author Graham Willis
 */
public class HierarchyGroupLevel<E extends MasterDataEntity> extends HierarchyLevel<E> {
    private final String groupPropertyName;

    private final StringPath groupFieldPath;

    private final Method groupFieldGetter;

    public HierarchyGroupLevel(Class<E> hierarchyEntity,
            ControlledListsList controlledList,
            String groupPropertyName) {
        super(hierarchyEntity, controlledList);
        this.groupPropertyName = groupPropertyName;
        this.groupFieldPath = EntityPathUtils.getStringPath(hierarchyEntity, groupPropertyName);

        try {
            this.groupFieldGetter = hierarchyEntity.getDeclaredMethod("get" + StringUtils.capitalize(groupPropertyName));
        } catch (ReflectiveOperationException e) {
            throw new RuntimeException("Unable to create hierarchy group level", e);
        }
    }

    public String getGroupPropertyName() {
        return groupPropertyName;
    }

    public StringPath getGroupFieldPath() {
        return groupFieldPath;
    }

    public Method getGroupFieldGetter() {
        return groupFieldGetter;
    }
}
