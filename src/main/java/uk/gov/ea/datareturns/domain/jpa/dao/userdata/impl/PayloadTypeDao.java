package uk.gov.ea.datareturns.domain.jpa.dao.userdata.impl;

import org.springframework.stereotype.Repository;
import uk.gov.ea.datareturns.domain.jpa.dao.masterdata.impl.EntityCache;
import uk.gov.ea.datareturns.domain.jpa.dao.userdata.AbstractUserDataDao;
import uk.gov.ea.datareturns.domain.jpa.entities.userdata.impl.PayloadType;
import uk.gov.ea.datareturns.util.CachingSupplier;

import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;

/**
 * @author Graham Willis
 */
@Repository
public class PayloadTypeDao extends AbstractUserDataDao {
    private static final String CACHE_ALL = "CACHE_ALL";
    private final CachingSupplier<EntityCache<String, PayloadType>> cache = CachingSupplier.of(this::cacheBuilder);

    public PayloadTypeDao() {
        super(PayloadType.class);
    }

    public PayloadType get(String payloadTypeName) {
        return getCache().defaultView().get(payloadTypeName);
    }

    protected EntityCache<String, PayloadType> cacheBuilder() {
        return EntityCache.build(() -> {
            CriteriaBuilder cb = entityManager.getCriteriaBuilder();
            CriteriaQuery<PayloadType> q = cb.createQuery(PayloadType.class);
            Root<PayloadType> c = q.from(PayloadType.class);
            q.select(c);
            TypedQuery<PayloadType> query = entityManager.createQuery(q);
            return query.getResultList();
        }, EntityCache.View.of(CACHE_ALL, e -> e.getPayloadTypeName()));
    }

    protected final EntityCache<String, PayloadType>  getCache() {
        return cache.get();
    }
}
