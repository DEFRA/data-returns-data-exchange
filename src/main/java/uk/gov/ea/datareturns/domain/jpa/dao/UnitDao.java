package uk.gov.ea.datareturns.domain.jpa.dao;

import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Repository;
import uk.gov.ea.datareturns.domain.jpa.entities.Unit;

/**
 * DAO for units of measure.
 *
 * @author Sam Gardner-Dell
 */
@Repository
@Scope(ConfigurableBeanFactory.SCOPE_SINGLETON)
public class UnitDao extends AliasingEntityDao<Unit> {
    public UnitDao() {
        super(Unit.class);
    }

    // Allow for no spaces in the method or standard
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
}