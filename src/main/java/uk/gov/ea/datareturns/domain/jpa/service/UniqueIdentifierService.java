package uk.gov.ea.datareturns.domain.jpa.service;

import org.springframework.stereotype.Component;
import uk.gov.ea.datareturns.domain.jpa.dao.SiteDao;
import uk.gov.ea.datareturns.domain.jpa.dao.UniqueIdentifierAliasDao;
import uk.gov.ea.datareturns.domain.jpa.dao.UniqueIdentifierDao;
import uk.gov.ea.datareturns.domain.jpa.entities.Site;
import uk.gov.ea.datareturns.domain.jpa.entities.UniqueIdentifier;
import uk.gov.ea.datareturns.domain.jpa.entities.UniqueIdentifierAlias;

import javax.inject.Inject;
import java.util.Set;

/**
 * The functions to support the EA_ID lookup and its aliases
 * @author Graham Willis
 */
@Component
public class UniqueIdentifierService {

    private UniqueIdentifierDao uniqueIdentifierDao;
    private UniqueIdentifierAliasDao uniqueIdentifierAliasDao;
    private SiteDao siteDao;

    @Inject
    public UniqueIdentifierService(UniqueIdentifierDao uniqueIdentifierDao, UniqueIdentifierAliasDao uniqueIdentifierAliasDao, SiteDao siteDao) {
        this.uniqueIdentifierDao = uniqueIdentifierDao;
        this.uniqueIdentifierAliasDao = uniqueIdentifierAliasDao;
        this.siteDao = siteDao;
    }

    /**
     * Determine the UniqueIdentifier from its name or aliases name
     * @param name or aliases name
     * @return
     */
    public UniqueIdentifier getUniqueIdentifier(String name) {
        UniqueIdentifier ui = uniqueIdentifierDao.getByName(name);
        if (ui != null) {
            return ui;
        } else {
            UniqueIdentifierAlias uia = uniqueIdentifierAliasDao.getByName(name);
            if (uia != null) {
                return uia.getUniqueIdentifier();
            } else {
                return null;
            }
        }

    }

    /**
     * Test if a unique identifier from its name or alias name
     * @param name
     * @return
     */
    public boolean uniqueIdentifierExists(String name) {
        return getUniqueIdentifier(name) != null ? true : false;
    }

    /**
     * Get the site from the unique identifier
     * @param uniqueIdentifier
     * @return The site
     */
    public Site getSite(UniqueIdentifier uniqueIdentifier) {
        return uniqueIdentifier.getSite();
    }

    /**
     * Get the site from the unique identifier alias
     * @param uniqueIdentifierAlias
     * @return the site
     */
    public Site getSite(UniqueIdentifierAlias uniqueIdentifierAlias) {
        return uniqueIdentifierAlias.getUniqueIdentifier().getSite();
    }

    /**
     * Get the list of all permit numbers from the uniqueIdentifierName
     * @param name unique identifier name
     * @return List of all permit numbers
     */
    public Set<String> getAllUniqueIdentifierNames(String name) {
        UniqueIdentifier ui = getUniqueIdentifier(name);
        Set<String> aliasNames = uniqueIdentifierAliasDao.getAliasNames(ui);
        aliasNames.add(ui.getName());
        return aliasNames;
    }

}
