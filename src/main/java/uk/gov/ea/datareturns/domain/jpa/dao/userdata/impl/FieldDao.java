package uk.gov.ea.datareturns.domain.jpa.dao.userdata.impl;

import org.springframework.stereotype.Repository;
import uk.gov.ea.datareturns.domain.jpa.dao.masterdata.impl.EntityCache;
import uk.gov.ea.datareturns.domain.jpa.dao.userdata.AbstractUserDataDao;
import uk.gov.ea.datareturns.domain.jpa.entities.userdata.impl.FieldEntity;
import uk.gov.ea.datareturns.domain.jpa.entities.userdata.impl.FieldId;
import uk.gov.ea.datareturns.domain.jpa.entities.userdata.impl.PayloadType;
import uk.gov.ea.datareturns.util.CachingSupplier;

import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author Graham Willis
 */
@Repository
public class FieldDao extends AbstractUserDataDao {
    private static final String CACHE_ALL = "CACHE_ALL";
    private final CachingSupplier<EntityCache<FieldId, FieldEntity>> cache = CachingSupplier.of(this::cacheBuilder);

    public FieldDao() {
        super(FieldEntity.class);
    }

    public FieldEntity get(PayloadType payloadType, String fieldName) {
        FieldId id = new FieldId();
        id.setFieldName(fieldName);
        id.setPayloadType(payloadType);
        return getCache().defaultView().get(id);
    }

    protected EntityCache<FieldId, FieldEntity> cacheBuilder() {
        return EntityCache.build(() -> {
            CriteriaBuilder cb = entityManager.getCriteriaBuilder();
            CriteriaQuery<FieldEntity> q = cb.createQuery(FieldEntity.class);
            Root<FieldEntity> c = q.from(FieldEntity.class);
            q.select(c);
            TypedQuery<FieldEntity> query = entityManager.createQuery(q);
            return query.getResultList();
        }, EntityCache.View.of(CACHE_ALL, FieldEntity::getId));
    }

    protected final EntityCache<FieldId, FieldEntity>  getCache() {
        return cache.get();
    }

    public List<FieldEntity> list(PayloadType payloadEntityType) {
        return getCache()
                .defaultView()
                .values()
                .stream()
                .filter(p -> p.getId().getPayloadType().equals(payloadEntityType))
                .collect(Collectors.toList());
    }
}
