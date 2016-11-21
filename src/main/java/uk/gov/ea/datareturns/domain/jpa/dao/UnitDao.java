package uk.gov.ea.datareturns.domain.jpa.dao;

import org.springframework.context.annotation.Scope;
import org.springframework.context.annotation.ScopedProxyMode;
import org.springframework.stereotype.Repository;
import uk.gov.ea.datareturns.domain.jpa.entities.Unit;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

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

    @Override
    public Set<String> listGroups() {
        // TODO
        Set<String> result = new HashSet<>();
        result.add("Radioactive");
        result.add("Mass");
        return result;
    }

    @Override
    public List<Unit> listGroupMembers(String group) {
        // TODO
        return list();
    }

    @Override
    public boolean testGroupMember(String group, Unit item) {
        // TODO
        return false;
    }
}