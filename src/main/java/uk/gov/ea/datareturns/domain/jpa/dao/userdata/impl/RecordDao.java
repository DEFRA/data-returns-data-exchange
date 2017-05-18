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
public class RecordDao extends AbstractUserDataDao<RecordEntity> {

    /**
     * Let the Dao class know the type of entity in order that type-safe
     * hibernate operations can be performed
     *
     */
    public RecordDao() {
        super(RecordEntity.class);
    }

    /**
     * Get a given record for a dataset and identifier
     * @param dataset The dataset
     * @param identifier The identifier
     * @return
     */
    public RecordEntity get(DatasetEntity dataset, String identifier) {
        CriteriaBuilder cb = entityManager.getCriteriaBuilder();
        Metamodel m = entityManager.getMetamodel();
        CriteriaQuery<RecordEntity> cq = cb.createQuery(RecordEntity.class);
        Root<RecordEntity> record = cq.from(m.entity(RecordEntity.class));

        cq.select(record);
        cq.where(
                cb.equal(record.get(RecordEntity_.identifier), identifier),
                cb.equal(record.get(RecordEntity_.dataset), dataset)
        );

        TypedQuery<RecordEntity> q = entityManager.createQuery(cq);

        try {
            RecordEntity r = q.getSingleResult();
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
    public List<RecordEntity> list(DatasetEntity dataset) {
        CriteriaBuilder cb = entityManager.getCriteriaBuilder();
        Metamodel m = entityManager.getMetamodel();
        CriteriaQuery<RecordEntity> cq = cb.createQuery(RecordEntity.class);
        Root<RecordEntity> record = cq.from(m.entity(RecordEntity.class));
        cq.where(cb.equal(record.get(RecordEntity_.dataset), dataset));
        cq.select(record);
        TypedQuery<RecordEntity> q = entityManager.createQuery(cq);
        return q.getResultList();
    }

    /**
     * Remove a single record by its identifier and dataset
     * @param dataset
     * @param identifier
     */
    public void remove(DatasetEntity dataset, String identifier) {
        RecordEntity recordEntity = get(dataset, identifier);
        remove(recordEntity.getId());
    }

    public List<RecordEntity> listMeasurements(DatasetEntity dataset) {
        CriteriaBuilder cb = entityManager.getCriteriaBuilder();
        CriteriaQuery<RecordEntity> cq = cb.createQuery(RecordEntity.class);

        Root<RecordEntity> record = cq.from(RecordEntity.class);

        cq.select(record);
        cq.where(cb.equal(record.get(RecordEntity_.dataset), dataset));
        cq.orderBy(cb.desc(record.get(RecordEntity_.id)));

        TypedQuery<RecordEntity> tq = this.entityManager.createQuery(cq);
        return tq.getResultList();
    }

    public RecordEntity getMeasurement(DatasetEntity dataset, String identifier) {
        CriteriaBuilder cb = entityManager.getCriteriaBuilder();
        CriteriaQuery<RecordEntity> cq = cb.createQuery(RecordEntity.class);

        Root<RecordEntity> record = cq.from(RecordEntity.class);

        cq.select(record);
        cq.where(
                cb.equal(record.get(RecordEntity_.dataset), dataset),
                cb.equal(record.get(RecordEntity_.identifier), identifier)
        );
        cq.orderBy(cb.desc(record.get(RecordEntity_.id)));

        TypedQuery<RecordEntity> tq = this.entityManager.createQuery(cq);

        return tq.getSingleResult();
    }

}
