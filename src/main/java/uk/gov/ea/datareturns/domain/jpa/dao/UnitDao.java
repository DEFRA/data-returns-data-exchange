package uk.gov.ea.datareturns.domain.jpa.dao;

import org.springframework.context.annotation.Scope;
import org.springframework.context.annotation.ScopedProxyMode;
import org.springframework.stereotype.Repository;
import uk.gov.ea.datareturns.domain.jpa.entities.Unit;

import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * DAO for units of measure.
 *
 * @author Sam Gardner-Dell
 */
@Repository
@Scope(proxyMode = ScopedProxyMode.TARGET_CLASS)
public class UnitDao extends AliasingEntityDao<Unit> implements GroupingEntityDao<Unit> {
    public UnitDao() {
        super(Unit.class);
    }

    private volatile Map<String, Set<Unit>> cacheByGroup = null;

    public String getKeyFromRelaxedName(String name) {
        return name == null ? null : name.trim();
    }

    // Override this we don't want to use the key cache here
    @Override
    public String getStandardizedName(final String name) {
        Unit unit = getByAlias(name);
        if (unit != null) {
            return unit.getName();
        } else {
            unit = getCache().get(name);
            if (unit != null) {
                return unit.getName();
            } else {
                return null;
            }
        }
    }

    private Map<String, Set<Unit>> getCacheByGroup() {
        if (cacheByGroup == null) {
            synchronized (this) {
                if (cacheByGroup == null) {
                    LOGGER.info("Build key cache of: Unit type");
                    cacheByGroup = list().stream().collect(Collectors.groupingBy(Unit::getType, Collectors.toSet()));
                }
            }
        }
        return cacheByGroup;
    }

    @Override
    public Set<String> listGroups() {
        return getCacheByGroup().entrySet().stream().map(Map.Entry::getKey).collect(Collectors.toSet());

    }

    @Override
    public Set<Unit> getGroupMembers(String group) {
        return getCacheByGroup().get(group);
    }

    @Override
    public boolean isGroupMember(String group, String item) {
        Unit unit = getByName(item);
        return unit.getType().equals(group);
    }
}