package uk.gov.ea.datareturns.domain.jpa.dao;

import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Repository;
import uk.gov.ea.datareturns.domain.jpa.entities.Site;

/**
 * DAO for return periods
 *
 * @author Sam Gardner-Dell
 */
@Repository
@Scope(ConfigurableBeanFactory.SCOPE_SINGLETON)
public class SiteDao extends EntityDao<Site> {
    public SiteDao() {
        super(Site.class);
    }

    // Site names are exact and cases sensitive
    public String getKeyFromRelaxedName(String name) {
        return name;
    }

    // Just hit the base cache
    public Site getByNameRelaxed(String name) {
        return getKeyCache().get(name);
    }

}