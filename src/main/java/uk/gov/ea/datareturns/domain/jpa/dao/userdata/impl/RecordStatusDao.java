package uk.gov.ea.datareturns.domain.jpa.dao.userdata.impl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Repository;
import uk.gov.ea.datareturns.domain.jpa.dao.masterdata.impl.EntityCache;
import uk.gov.ea.datareturns.domain.jpa.entities.userdata.impl.RecordStatus;
import uk.gov.ea.datareturns.domain.jpa.service.SubmissionService;
import uk.gov.ea.datareturns.util.CachingSupplier;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
import java.util.List;

/**
 * @author Graham Willis
 */
@Repository
@Scope(ConfigurableBeanFactory.SCOPE_SINGLETON)
public class RecordStatusDao {
    protected static final Logger LOGGER = LoggerFactory.getLogger(RecordStatusDao.class);

    private final CachingSupplier<EntityCache<String, RecordStatus>> cache = CachingSupplier.of(this::cacheBuilder);
    protected final String CACHE_ALL_ENTITIES = "CACHE_ALL_ENTITIES";

    @PersistenceContext
    protected EntityManager entityManager;

    public RecordStatus getStatus(String status) {
        return getCache().defaultView().get(status);
    }

    protected EntityCache<String, RecordStatus> cacheBuilder() {
        return EntityCache.build(fetchAll(), EntityCache.View.of(CACHE_ALL_ENTITIES, RecordStatus::getStatus));
    }

    /**
     * Retrieve the {@link EntityCache}
     */
    protected final EntityCache<String, RecordStatus> getCache() {
        return cache.get();
    }

    protected final List<RecordStatus> fetchAll() {
        LOGGER.info("Building cache of: RecordStatus");
        CriteriaBuilder cb = entityManager.getCriteriaBuilder();
        CriteriaQuery<RecordStatus> q = cb.createQuery(RecordStatus.class);
        Root<RecordStatus> c = q.from(RecordStatus.class);
        q.select(c);
        TypedQuery<RecordStatus> query = entityManager.createQuery(q);
        return query.getResultList();
    }
}
