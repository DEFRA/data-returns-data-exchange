package uk.gov.ea.datareturns.domain.jpa.dao.userdata.impl;

import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Repository;
import uk.gov.ea.datareturns.domain.jpa.dao.userdata.AbstractUserDataDao;
import uk.gov.ea.datareturns.domain.jpa.entities.userdata.impl.Dataset;
import uk.gov.ea.datareturns.domain.jpa.entities.userdata.impl.Dataset_;
import uk.gov.ea.datareturns.domain.jpa.entities.userdata.impl.User;

import javax.persistence.NoResultException;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
import javax.persistence.metamodel.Metamodel;
import java.util.List;

/**
 * @author Graham Willis
 */
@Repository
@Scope(ConfigurableBeanFactory.SCOPE_SINGLETON)
public class DatasetDao extends AbstractUserDataDao<Dataset> {

    /**
     * Let the Dao class know the type of entity in order that type-safe
     * hibernate operations can be performed
     */
    public DatasetDao() {
        super(Dataset.class);
    }

    public Dataset get(String identifier) {
        CriteriaBuilder cb = entityManager.getCriteriaBuilder();
        Metamodel m = entityManager.getMetamodel();
        CriteriaQuery<Dataset> cq = cb.createQuery(Dataset.class);
        Root<Dataset> dataset = cq.from(m.entity(Dataset.class));
        cq.where(cb.equal(dataset.get(Dataset_.identifier), identifier));
        cq.select(dataset);
        TypedQuery<Dataset> q = entityManager.createQuery(cq);
        try {
            Dataset d = q.getSingleResult();
            return d;
        } catch (NoResultException e) {
            // If there are no results just return null
            return null;
        }
    }

    public List<Dataset> list(User user) {
        CriteriaBuilder cb = entityManager.getCriteriaBuilder();
        Metamodel m = entityManager.getMetamodel();
        CriteriaQuery<Dataset> cq = cb.createQuery(Dataset.class);
        Root<Dataset> dataset = cq.from(m.entity(Dataset.class));
        cq.where(cb.equal(dataset.get(Dataset_.user), user));
        cq.select(dataset);
        TypedQuery<Dataset> q = entityManager.createQuery(cq);
        return q.getResultList();
    }

    public void remove(String identifier) {
        Dataset ds = get(identifier);
        remove(ds.getId());
    }
}
