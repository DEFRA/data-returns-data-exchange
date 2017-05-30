package uk.gov.ea.datareturns.domain.jpa.dao.userdata.impl;

import org.springframework.stereotype.Repository;
import uk.gov.ea.datareturns.domain.jpa.dao.masterdata.impl.EntityCache;
import uk.gov.ea.datareturns.domain.jpa.entities.userdata.impl.PayloadType;
import uk.gov.ea.datareturns.domain.jpa.entities.userdata.impl.ValidationError;
import uk.gov.ea.datareturns.domain.jpa.entities.userdata.impl.ValidationErrorId;
import uk.gov.ea.datareturns.util.CachingSupplier;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author Graham Willis
 */
@Repository
public class ValidationErrorDao {

    private static final String CACHE_ALL = "CACHE_ALL";

    private final CachingSupplier<EntityCache<ValidationErrorId, ValidationError>> cache = CachingSupplier.of(this::cacheBuilder);

    @PersistenceContext
    protected EntityManager entityManager;

    public ValidationError get(ValidationErrorId id) {
        return getCache().defaultView().get(id);
    }

    public List<ValidationError> list() {
        return new ArrayList<>(getCache().defaultView().values());
    }

    public List<ValidationError> list(PayloadType payloadType) {
        return getCache().defaultView().values().stream().filter(v -> v.getId().getPayloadType() == payloadType).collect(Collectors.toList());
    }

    protected EntityCache<ValidationErrorId, ValidationError> cacheBuilder() {
        return EntityCache.build(() -> {
            CriteriaBuilder cb = entityManager.getCriteriaBuilder();
            CriteriaQuery<ValidationError> q = cb.createQuery(ValidationError.class);
            Root<ValidationError> c = q.from(ValidationError.class);
            q.select(c);
            TypedQuery<ValidationError> query = entityManager.createQuery(q);
            return query.getResultList();
        }, EntityCache.View.of(CACHE_ALL, e -> e.getId()));
    }

    protected final EntityCache<ValidationErrorId, ValidationError> getCache() {
        return cache.get();
    }
}
