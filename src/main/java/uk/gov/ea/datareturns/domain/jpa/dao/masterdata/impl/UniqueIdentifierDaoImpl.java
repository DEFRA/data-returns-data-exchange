package uk.gov.ea.datareturns.domain.jpa.dao.masterdata.impl;

import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Repository;
import uk.gov.ea.datareturns.domain.jpa.dao.masterdata.EntityDao;
import uk.gov.ea.datareturns.domain.jpa.dao.masterdata.Key;
import uk.gov.ea.datareturns.domain.jpa.dao.masterdata.UniqueIdentifierDao;
import uk.gov.ea.datareturns.domain.jpa.entities.masterdata.impl.Site;
import uk.gov.ea.datareturns.domain.jpa.entities.masterdata.impl.UniqueIdentifier;
import uk.gov.ea.datareturns.domain.jpa.entities.masterdata.impl.UniqueIdentifierAlias;
import uk.gov.ea.datareturns.util.CachingSupplier;

import javax.inject.Inject;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * DAO for Unique Identifiers
 *
 * The Unique identifier (EA_ID) and its aliases do NOT use the
 * standard aliasing mechanism. (This is because the data structure
 * differs for the entities uniqueIdentifier, uniqueIdentifierAlias and Site
 *
 * The UniqueIdentifiersService is the service level aggregator for teh functionality
 * connecting these entities
 *
 * @author Graham Willis
 */
@Repository
@Scope(ConfigurableBeanFactory.SCOPE_SINGLETON)
public class UniqueIdentifierDaoImpl extends AbstractEntityDao<UniqueIdentifier> implements UniqueIdentifierDao {
    private final CachingSupplier<Map<String, Set<String>>> cacheAliasByBaseName = CachingSupplier.of(this::aliasCacheSupplier);
    private final CachingSupplier<Map<String, Set<UniqueIdentifier>>> cacheBaseNameBySite =
            CachingSupplier.of(this::permitBySiteCacheSupplier);

    private EntityDao<UniqueIdentifierAlias> uniqueIdentifierAliasDao;

    @Inject
    public UniqueIdentifierDaoImpl(EntityDao<UniqueIdentifierAlias> uniqueIdentifierAliasDao, ApplicationEventPublisher publisher) {
        super(UniqueIdentifier.class, publisher);
        this.uniqueIdentifierAliasDao = uniqueIdentifierAliasDao;
    }

    // Do not allow any relaxation when looking for EA_ID's they should always be exact
    @Override public String generateMash(String inputValue) {
        return inputValue;
    }

    public UniqueIdentifier getByNameOrAlias(Key lookup) {
        UniqueIdentifier ui = getByName(lookup);
        if (ui == null) {
            UniqueIdentifierAlias uia = uniqueIdentifierAliasDao.getByName(lookup);
            ui = uia != null ? uia.getUniqueIdentifier() : null;
        }
        return ui;
    }

    @Override public UniqueIdentifier getPreferred(Key nameOrAlias) {
        return getByNameOrAlias(nameOrAlias);
    }

    /**
     * Get list of alias names for a given UniqueIdentifier
     * @param uniqueIdentifier
     * @return Alias names
     */
    @Override public Set<String> getAliasNames(UniqueIdentifier uniqueIdentifier) {
        return getAliasCache().get(uniqueIdentifier.getName());
    }

    /**
     * Get a unique identifier from (exact) site name
     */
    @Override public Set<UniqueIdentifier> getUniqueIdentifierBySiteName(String siteName) {
        return getCacheBaseNameBySite().get(siteName);
    }

    /**
     * Test if a unique identifier from its name or alias name
     * @param name
     * @return
     */
    @Override public boolean uniqueIdentifierExists(String name) {
        return getByNameOrAlias(Key.explicit(name)) != null;
    }

    /**
     * Get the site from the unique identifier
     * @param uniqueIdentifier
     * @return The site
     */
    @Override public Site getSite(UniqueIdentifier uniqueIdentifier) {
        return uniqueIdentifier.getSite();
    }

    /**
     * Get the site from the unique identifier alias
     * @param uniqueIdentifierAlias
     * @return the site
     */
    @Override public Site getSite(UniqueIdentifierAlias uniqueIdentifierAlias) {
        return uniqueIdentifierAlias.getUniqueIdentifier().getSite();
    }

    /**
     * Get the list of all permit numbers from the uniqueIdentifierName
     * @param name unique identifier name
     * @return List of all permit numbers
     */
    @Override public Set<String> getAllUniqueIdentifierNames(String name) {
        // Try lookup on primary unique identifiers
        UniqueIdentifier ui = getByNameOrAlias(Key.explicit(name));
        Set<String> aliasNames = new HashSet<>();
        if (ui != null) {
            aliasNames = getAliasNames(ui);
            aliasNames.add(ui.getName());
        }
        return aliasNames;
    }

    /**
     * Build the cache lookup for the set of permit numbers by the base permit number
     * @return
     */
    private Map<String, Set<String>> getAliasCache() {
        return cacheAliasByBaseName.get();
    }

    private Map<String, Set<UniqueIdentifier>> getCacheBaseNameBySite() {
        return cacheBaseNameBySite.get();
    }

    /**
     * Build the cache lookup for the set of permit numbers by the base permit number
     * @return
     */
    private Map<String, Set<String>> aliasCacheSupplier() {
        LOGGER.info("Build cache: Alias by base name");
        List<UniqueIdentifierAlias> list = uniqueIdentifierAliasDao.list();
        return list.stream().collect(
                Collectors.groupingBy(e -> e.getUniqueIdentifier().getName(),
                        Collectors.mapping(UniqueIdentifierAlias::getName, Collectors.toSet())
                )
        );
    }

    private Map<String, Set<UniqueIdentifier>> permitBySiteCacheSupplier() {
        LOGGER.info("Build cache: Base name by site");
        List<UniqueIdentifier> list = list();
        return list.stream().collect(Collectors.groupingBy(e -> e.getSite().getName(), Collectors.toSet()));
    }
}