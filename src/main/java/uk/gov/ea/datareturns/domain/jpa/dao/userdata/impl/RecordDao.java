package uk.gov.ea.datareturns.domain.jpa.dao.userdata.impl;

import org.apache.commons.lang3.tuple.Triple;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Repository;
import uk.gov.ea.datareturns.domain.jpa.dao.userdata.AbstractUserDataDao;
import uk.gov.ea.datareturns.domain.jpa.entities.userdata.impl.DatasetEntity;
import uk.gov.ea.datareturns.domain.jpa.entities.userdata.impl.RecordEntity;
import uk.gov.ea.datareturns.domain.jpa.entities.userdata.impl.RecordEntity_;

import javax.persistence.NoResultException;
import javax.persistence.Query;
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

    public enum FetchType {
        FETCH_INVALID, FETCH_BASE
    }

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
     * @param fetchType
     * @return a list of records
     */
    public List<RecordEntity> list(DatasetEntity dataset, FetchType fetchType) {
        CriteriaBuilder cb = entityManager.getCriteriaBuilder();
        Metamodel m = entityManager.getMetamodel();
        CriteriaQuery<RecordEntity> cq = cb.createQuery(RecordEntity.class);
        Root<RecordEntity> record = cq.from(m.entity(RecordEntity.class));

        if (fetchType == FetchType.FETCH_INVALID) {
            record.fetch(RecordEntity_.validationErrors);
            cq.where(
                    cb.equal(record.get(RecordEntity_.recordStatus), RecordEntity.RecordStatus.INVALID),
                    cb.equal(record.get(RecordEntity_.dataset), dataset)
            );
        } else if (fetchType == FetchType.FETCH_BASE) {
            cq.where(cb.equal(record.get(RecordEntity_.dataset), dataset));
        }
        cq.select(record);
        TypedQuery<RecordEntity> q = entityManager.createQuery(cq);
        return q.getResultList();
    }

    /**
     * Retrieve the validation errors as a pair of strings; the record identifier and the error identifier
     * for a give dataset
     * @param dataset
     * @return The validation errors for a record
     */
    public List<Triple<String, String, String>> getValidationErrors(DatasetEntity dataset) {
        final String QRY_STR =
                "select r.identifier, r.payload_type, e.error" +
                "  from records r" +
                "  join record_validation_errors e" +
                "    on r.id = e.record_id" +
                "  join datasets d" +
                "    on r.dataset_id = d.id" +
                " where d.identifier = :ds_id";

        Query nativeQuery = entityManager
                .createNativeQuery(QRY_STR, "selectValidationErrorsMapping");

        nativeQuery.setParameter("ds_id", dataset.getIdentifier());

        return nativeQuery.getResultList();
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

    /**
     * Remove a given record
     * @param recordEntity
     */
    public void remove(RecordEntity recordEntity) {
        remove(recordEntity.getId());
    }

}
