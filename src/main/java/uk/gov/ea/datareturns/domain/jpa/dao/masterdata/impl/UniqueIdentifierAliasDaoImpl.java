package uk.gov.ea.datareturns.domain.jpa.dao.masterdata.impl;

import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Repository;
import uk.gov.ea.datareturns.domain.jpa.dao.masterdata.UniqueIdentifierAliasDao;
import uk.gov.ea.datareturns.domain.jpa.entities.masterdata.impl.UniqueIdentifier;
import uk.gov.ea.datareturns.domain.jpa.entities.masterdata.impl.UniqueIdentifierAlias;
import uk.gov.ea.datareturns.domain.jpa.entities.masterdata.impl.UniqueIdentifierAlias_;
import uk.gov.ea.datareturns.domain.jpa.entities.masterdata.impl.UniqueIdentifier_;

import javax.inject.Inject;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.*;
import java.util.List;

/**
 * DAO for unique identifier aliases
 *
 * @author Graham Willis
 */
@Repository
@Scope(ConfigurableBeanFactory.SCOPE_SINGLETON)
public class UniqueIdentifierAliasDaoImpl extends AbstractEntityDao<UniqueIdentifierAlias> implements UniqueIdentifierAliasDao {

    @Inject
    public UniqueIdentifierAliasDaoImpl() {
        super(UniqueIdentifierAlias.class);
    }

    // Do not allow any relaxation when looking for EA_ID's they should always be exact
    @Override public String generateMash(String inputValue) {
        return inputValue;
    }

    protected final List<UniqueIdentifierAlias> fetchAll() {
        CriteriaBuilder cb = entityManager.getCriteriaBuilder();
        CriteriaQuery<UniqueIdentifierAlias> q = cb.createQuery(UniqueIdentifierAlias.class);
        Root<UniqueIdentifierAlias> c = q.from(UniqueIdentifierAlias.class);
        Fetch<UniqueIdentifierAlias, UniqueIdentifier> uniqueIdentifier = c.fetch(UniqueIdentifierAlias_.uniqueIdentifier, JoinType.INNER);
        uniqueIdentifier.fetch(UniqueIdentifier_.site);
        q.select(c);
        TypedQuery<UniqueIdentifierAlias> query = entityManager.createQuery(q);
        return query.getResultList();
    }

}