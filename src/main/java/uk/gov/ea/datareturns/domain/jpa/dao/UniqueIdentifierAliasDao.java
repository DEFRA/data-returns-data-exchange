package uk.gov.ea.datareturns.domain.jpa.dao;

import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Repository;
import uk.gov.ea.datareturns.domain.jpa.entities.UniqueIdentifier;
import uk.gov.ea.datareturns.domain.jpa.entities.UniqueIdentifierAlias;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * DAO for return periods
 *
 * @author Graham Willis
 */
@Repository
@Scope(ConfigurableBeanFactory.SCOPE_SINGLETON)
public class UniqueIdentifierAliasDao extends EntityDao<UniqueIdentifierAlias> {

    private volatile Map<String, Set<String>> cacheAliasByBaseName = null;

    public UniqueIdentifierAliasDao() {
        super(UniqueIdentifierAlias.class);
    }

    // Do not allow any relaxation when looking for EA_ID's they should always be exact
    public String getKeyFromRelaxedName(String name) {
        return name;
    }

    // Just hit the base cache
    public UniqueIdentifierAlias getByNameRelaxed(String name) {
        return getKeyCache().get(name);
    }

    private Map<String, Set<String>> getAliasCache() {
        if (cacheAliasByBaseName == null) {
            synchronized (this) {
                if (cacheAliasByBaseName == null) {
                    LOGGER.info("Build base name cache of: UniqueIdentifierAlias");
                    List<UniqueIdentifierAlias> list = super.list();
                    cacheAliasByBaseName = list.stream().collect(
                            Collectors.groupingBy(e -> e.getUniqueIdentifier().getName(),
                                    Collectors.mapping(e -> e.getName(), Collectors.toSet())
                            )
                    );
                }
            }
        }
        return cacheAliasByBaseName;
    }

    /**
     * Get list of alias names for a given UniqueIdentifier
     * @param uniqueIdentifier
     * @return Alias names
     */
    public Set<String> getAliasNames(UniqueIdentifier uniqueIdentifier) {
        return getAliasCache().get(uniqueIdentifier.getName());
    }

}