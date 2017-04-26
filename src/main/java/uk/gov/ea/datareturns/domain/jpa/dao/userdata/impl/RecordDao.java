package uk.gov.ea.datareturns.domain.jpa.dao.userdata.impl;

import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Repository;
import uk.gov.ea.datareturns.domain.jpa.dao.userdata.AbstractUserDataDao;
import uk.gov.ea.datareturns.domain.jpa.entities.userdata.impl.Dataset;
import uk.gov.ea.datareturns.domain.jpa.entities.userdata.impl.Dataset_;
import uk.gov.ea.datareturns.domain.jpa.entities.userdata.impl.Record;
import uk.gov.ea.datareturns.domain.jpa.entities.userdata.impl.Record_;

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
public class RecordDao extends AbstractUserDataDao<Record> {

    /**
     * Let the Dao class know the type of entity in order that type-safe
     * hibernate operations can be performed
     *
     */
    public RecordDao() {
        super(Record.class);
    }

    @Override
    public Record getByIdentifier(String identifier) {
        CriteriaBuilder cb = entityManager.getCriteriaBuilder();
        Metamodel m = entityManager.getMetamodel();
        CriteriaQuery<Record> cq = cb.createQuery(Record.class);
        Root<Record> record = cq.from(m.entity(Record.class));
        cq.where(cb.equal(record.get(Record_.identifier), identifier));
        cq.select(record);
        TypedQuery<Record> q = entityManager.createQuery(cq);
        try {
            Record r = q.getSingleResult();
            return r;
        } catch (NoResultException e) {
            // If there are no results just return null
            return null;
        }
    }

    public List<Record> list(Dataset dataset) {
        CriteriaBuilder cb = entityManager.getCriteriaBuilder();
        Metamodel m = entityManager.getMetamodel();
        CriteriaQuery<Record> cq = cb.createQuery(Record.class);
        Root<Record> record = cq.from(m.entity(Record.class));
        cq.where(cb.equal(record.get(Record_.dataset), dataset));
        cq.select(record);
        TypedQuery<Record> q = entityManager.createQuery(cq);
        return q.getResultList();
    }
}
