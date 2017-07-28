package uk.gov.ea.datareturns.domain.jpa.dao.userdata.impl;

import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Repository;
import uk.gov.ea.datareturns.domain.jpa.dao.userdata.AbstractUserDataDao;
import uk.gov.ea.datareturns.domain.jpa.entities.userdata.impl.User;
import uk.gov.ea.datareturns.domain.jpa.entities.userdata.impl.User_;

import javax.persistence.NoResultException;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
import javax.persistence.metamodel.Metamodel;

/**
 * @author Graham Willis
 */
@Repository
@Scope(ConfigurableBeanFactory.SCOPE_SINGLETON)
public class UserDao extends AbstractUserDataDao<User> {

    public UserDao() {
        super(User.class);
    }

    public User getSystemUser() {
        return get(User.SYSTEM);
    }

    public User get(String identifier) {
        CriteriaBuilder cb = entityManager.getCriteriaBuilder();
        Metamodel m = entityManager.getMetamodel();
        CriteriaQuery<User> cq = cb.createQuery(User.class);
        Root<User> user = cq.from(m.entity(User.class));
        cq.where(cb.equal(user.get(User_.identifier), identifier));
        cq.select(user);
        TypedQuery<User> q = entityManager.createQuery(cq);
        try {
            User u = q.getSingleResult();
            return u;
        } catch (NoResultException e) {
            // If there are no results just return null
            return null;
        }
    }

    public void remove(String identifier) {
        User user = get(identifier);
        remove(user.getId());
    }
}
