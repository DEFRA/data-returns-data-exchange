package uk.gov.ea.datareturns.domain.jpa.dao.masterdata.impl;

import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Repository;
import uk.gov.ea.datareturns.domain.jpa.dao.masterdata.SiteDao;
import uk.gov.ea.datareturns.domain.jpa.entities.masterdata.impl.Site;
import uk.gov.ea.datareturns.util.TextUtils;

import javax.inject.Inject;

/**
 * DAO for site names
 *
 * @author Sam Gardner-Dell
 */
@Repository
@Scope(value = ConfigurableBeanFactory.SCOPE_SINGLETON)
public class SiteDaoImpl extends AbstractEntityDao<Site> implements SiteDao {
    @Inject
    public SiteDaoImpl() {
        super(Site.class);
    }

    // Site names are case sensitive - trim whitespace
    public String generateMash(String inputValue) {
        return TextUtils.normalize(inputValue, TextUtils.WhitespaceHandling.REMOVE);
    }
}