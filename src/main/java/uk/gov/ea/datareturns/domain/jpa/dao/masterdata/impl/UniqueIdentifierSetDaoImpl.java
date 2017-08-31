package uk.gov.ea.datareturns.domain.jpa.dao.masterdata.impl;

import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Repository;
import uk.gov.ea.datareturns.domain.jpa.dao.masterdata.UniqueIdentifierSetDao;
import uk.gov.ea.datareturns.domain.jpa.entities.masterdata.impl.Operator;
import uk.gov.ea.datareturns.domain.jpa.entities.masterdata.impl.UniqueIdentifier;
import uk.gov.ea.datareturns.domain.jpa.entities.masterdata.impl.UniqueIdentifierSet;
import uk.gov.ea.datareturns.domain.jpa.entities.masterdata.impl.UniqueIdentifierSet_;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
import javax.persistence.metamodel.Metamodel;
import java.util.List;

/**
 * @author Graham Willis
 * Straightforward repository bean for sets
 */
@Repository
@Scope(ConfigurableBeanFactory.SCOPE_SINGLETON)
public class UniqueIdentifierSetDaoImpl implements UniqueIdentifierSetDao {

    @PersistenceContext
    protected EntityManager entityManager;

    @Override
    public List<UniqueIdentifierSet> listSetsFor(UniqueIdentifierSet.UniqueIdentifierSetType uniqueIdentifierSetType) {
        CriteriaBuilder cb = entityManager.getCriteriaBuilder();
        CriteriaQuery<UniqueIdentifierSet> q = cb.createQuery(UniqueIdentifierSet.class);
        Root<UniqueIdentifierSet> c = q.from(UniqueIdentifierSet.class);
        q.where(cb.equal(c.get(UniqueIdentifierSet_.uniqueIdentifierSetType), uniqueIdentifierSetType));
        q.select(c);
        TypedQuery<UniqueIdentifierSet> query = entityManager.createQuery(q);
        return query.getResultList();
    }

    @Override
    public List<UniqueIdentifierSet> listSetsFor(UniqueIdentifierSet.UniqueIdentifierSetType uniqueIdentifierSetType, Operator operator) {
        CriteriaBuilder cb = entityManager.getCriteriaBuilder();
        CriteriaQuery<UniqueIdentifierSet> q = cb.createQuery(UniqueIdentifierSet.class);
        Root<UniqueIdentifierSet> uniqueIdentifierSetRoot = q.from(UniqueIdentifierSet.class);
        q.where(
                cb.and(
                        cb.equal(uniqueIdentifierSetRoot.get(UniqueIdentifierSet_.uniqueIdentifierSetType), uniqueIdentifierSetType),
                        cb.equal(uniqueIdentifierSetRoot.get(UniqueIdentifierSet_.operator), operator)
                )
        );
        q.select(uniqueIdentifierSetRoot);
        TypedQuery<UniqueIdentifierSet> query = entityManager.createQuery(q);
        return query.getResultList();
    }

}
