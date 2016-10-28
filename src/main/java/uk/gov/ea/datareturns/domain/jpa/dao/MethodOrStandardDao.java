package uk.gov.ea.datareturns.domain.jpa.dao;

import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Repository;
import uk.gov.ea.datareturns.domain.jpa.entities.MethodOrStandard;

/**
 * DAO for monitoring methods and standards.
 *
 * @author Sam Gardner-Dell
 */
@Repository
@Scope(ConfigurableBeanFactory.SCOPE_SINGLETON)
public class MethodOrStandardDao extends EntityDao<MethodOrStandard> {
    public MethodOrStandardDao() {
        super(MethodOrStandard.class);
    }

    // Allow for no spaces in the method or standard
    public String getKeyFromRelaxedName(String name) {
        if (name != null) {
            return name.toUpperCase().trim().replaceAll("\\s", "");
        }
        return null;
    }

}