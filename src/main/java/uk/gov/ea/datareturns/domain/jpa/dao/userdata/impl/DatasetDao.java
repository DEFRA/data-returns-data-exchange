package uk.gov.ea.datareturns.domain.jpa.dao.userdata.impl;

import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Repository;
import uk.gov.ea.datareturns.domain.jpa.dao.userdata.AbstractUserDataDao;
import uk.gov.ea.datareturns.domain.jpa.entities.userdata.impl.DatasetEntity;
import uk.gov.ea.datareturns.domain.jpa.entities.userdata.impl.DatasetEntity_;
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
public class DatasetDao extends AbstractUserDataDao<DatasetEntity> {

    /**
     * Let the Dao class know the type of entity in order that type-safe
     * hibernate operations can be performed
     */
    public DatasetDao() {
        super(DatasetEntity.class);
    }

    public DatasetEntity get(User user, String identifier) {
        CriteriaBuilder cb = entityManager.getCriteriaBuilder();
        Metamodel m = entityManager.getMetamodel();
        CriteriaQuery<DatasetEntity> cq = cb.createQuery(DatasetEntity.class);
        Root<DatasetEntity> dataset = cq.from(m.entity(DatasetEntity.class));
        cq.select(dataset);
        cq.where(
                cb.equal(dataset.get(DatasetEntity_.identifier), identifier),
                cb.equal(dataset.get(DatasetEntity_.user), user)
        );
        TypedQuery<DatasetEntity> q = entityManager.createQuery(cq);
        try {
            DatasetEntity d = q.getSingleResult();
            return d;
        } catch (NoResultException e) {
            // If there are no results just return null
            return null;
        }
    }

    public List<DatasetEntity> list(User user) {
        CriteriaBuilder cb = entityManager.getCriteriaBuilder();
        Metamodel m = entityManager.getMetamodel();
        CriteriaQuery<DatasetEntity> cq = cb.createQuery(DatasetEntity.class);
        Root<DatasetEntity> dataset = cq.from(m.entity(DatasetEntity.class));
        cq.where(cb.equal(dataset.get(DatasetEntity_.user), user));
        cq.select(dataset);
        TypedQuery<DatasetEntity> q = entityManager.createQuery(cq);
        return q.getResultList();
    }

    public void remove(User user, String identifier) {
        DatasetEntity ds = get(user, identifier);
        remove(ds.getId());
    }
}
