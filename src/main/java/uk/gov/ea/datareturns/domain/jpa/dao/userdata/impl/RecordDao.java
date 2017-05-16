package uk.gov.ea.datareturns.domain.jpa.dao.userdata.impl;

import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Repository;
import uk.gov.ea.datareturns.domain.jpa.dao.userdata.AbstractUserDataDao;
import uk.gov.ea.datareturns.domain.jpa.entities.userdata.impl.*;

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

    /**
     * Get a given record for a dataset and identifier
     * @param dataset The dataset
     * @param identifier The identifier
     * @return
     */
    public Record get(DatasetEntity dataset, String identifier) {
        CriteriaBuilder cb = entityManager.getCriteriaBuilder();
        Metamodel m = entityManager.getMetamodel();
        CriteriaQuery<Record> cq = cb.createQuery(Record.class);
        Root<Record> record = cq.from(m.entity(Record.class));

        cq.select(record);
        cq.where(
                cb.equal(record.get(Record_.identifier), identifier),
                cb.equal(record.get(Record_.dataset), dataset)
        );

        TypedQuery<Record> q = entityManager.createQuery(cq);

        try {
            Record r = q.getSingleResult();
            return r;
        } catch (NoResultException e) {
            // If there are no results just return null
            return null;
        }
    }

    /**
     * Get a list of the records for a given dataset
     * @param dataset The dataset
     * @return a list of records
     */
    public List<Record> list(DatasetEntity dataset) {
        CriteriaBuilder cb = entityManager.getCriteriaBuilder();
        Metamodel m = entityManager.getMetamodel();
        CriteriaQuery<Record> cq = cb.createQuery(Record.class);
        Root<Record> record = cq.from(m.entity(Record.class));
        cq.where(cb.equal(record.get(Record_.dataset), dataset));
        cq.select(record);
        TypedQuery<Record> q = entityManager.createQuery(cq);
        return q.getResultList();
    }

    /**
     * Remove a single record by its identifier and dataset
     * @param dataset
     * @param identifier
     */
    public void remove(DatasetEntity dataset, String identifier) {
        Record record = get(dataset, identifier);
        remove(record.getId());
    }

    public List<Record> listMeasurements(DatasetEntity dataset) {
        CriteriaBuilder cb = entityManager.getCriteriaBuilder();
        CriteriaQuery<Record> cq = cb.createQuery(Record.class);

        Root<Record> record = cq.from(Record.class);

        cq.select(record);
        cq.where(cb.equal(record.get(Record_.dataset), dataset));
        cq.orderBy(cb.desc(record.get(Record_.id)));

        TypedQuery<Record> tq = this.entityManager.createQuery(cq);
        return tq.getResultList();
    }

    public Record getMeasurement(DatasetEntity dataset, String identifier) {
        CriteriaBuilder cb = entityManager.getCriteriaBuilder();
        CriteriaQuery<Record> cq = cb.createQuery(Record.class);

        Root<Record> record = cq.from(Record.class);

        cq.select(record);
        cq.where(
                cb.equal(record.get(Record_.dataset), dataset),
                cb.equal(record.get(Record_.identifier), identifier)
        );
        cq.orderBy(cb.desc(record.get(Record_.id)));

        TypedQuery<Record> tq = this.entityManager.createQuery(cq);

        return tq.getSingleResult();
    }

}
