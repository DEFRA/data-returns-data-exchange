package uk.gov.ea.datareturns.domain.jpa.dao.impl;

import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Repository;
import uk.gov.ea.datareturns.domain.jpa.dao.SiteDao;
import uk.gov.ea.datareturns.domain.jpa.entities.Site;

/**
 * DAO for site names
 *
 * @author Sam Gardner-Dell
 */
@Repository
@Scope(value = ConfigurableBeanFactory.SCOPE_SINGLETON)
public class SiteDaoImpl extends AbstractEntityDao<Site> implements SiteDao {
    public SiteDaoImpl() {
        super(Site.class);
    }

    // Site names are exact and cases sensitive
    public String generateMash(String inputValue) {
        return inputValue;
    }
}