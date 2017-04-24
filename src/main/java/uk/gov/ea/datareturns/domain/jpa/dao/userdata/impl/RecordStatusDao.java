package uk.gov.ea.datareturns.domain.jpa.dao.userdata.impl;

import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Repository;
import uk.gov.ea.datareturns.domain.jpa.dao.masterdata.impl.EntityCache;
import uk.gov.ea.datareturns.domain.jpa.entities.userdata.impl.RecordStatus;
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
    private final CachingSupplier<EntityCache<String, RecordStatus>> cache = CachingSupplier.of(this::cacheBuilder);

    @PersistenceContext
    protected EntityManager entityManager;

    public RecordStatus getStatus(String status) {
        return getCache().defaultView().get(status);
    }

    protected EntityCache<String, RecordStatus> cacheBuilder() {
        return EntityCache.build(fetchAll(), EntityCache.View.of("CACHE_ALL_ENTITIES", p -> p.getStatus()));
    }

    /**
     * Retrieve the {@link EntityCache}
     */
    protected final EntityCache<String, RecordStatus> getCache() {
        return cache.get();
    }

    List<RecordStatus> fetchAll() {
        CriteriaBuilder cb = entityManager.getCriteriaBuilder();
        CriteriaQuery<RecordStatus> q = cb.createQuery(RecordStatus.class);
        Root<RecordStatus> c = q.from(RecordStatus.class);
        q.select(c);
        TypedQuery<RecordStatus> query = entityManager.createQuery(q);
        return query.getResultList();
    }
}
