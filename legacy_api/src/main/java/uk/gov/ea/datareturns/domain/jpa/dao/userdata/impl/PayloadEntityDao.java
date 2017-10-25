package uk.gov.ea.datareturns.domain.jpa.dao.userdata.impl;

import uk.gov.ea.datareturns.domain.jpa.dao.userdata.AbstractUserDataDao;
import uk.gov.ea.datareturns.domain.jpa.entities.userdata.AbstractPayloadEntity;
import uk.gov.ea.datareturns.domain.jpa.entities.userdata.impl.DatasetEntity;

import javax.persistence.TypedQuery;
import java.util.List;

/**
 * @author Graham Willis
 */
public class PayloadEntityDao extends AbstractUserDataDao<AbstractPayloadEntity> {

    public PayloadEntityDao() {
        super(AbstractPayloadEntity.class);
    }

    /**
     * Retrieve the payload for the given recordId
     *
     * @param recordId the record id associated with the payload
     * @param
     * @return the payload, if found, null otherwise
     */
    public <P extends AbstractPayloadEntity> P find(Long recordId, Class<P> payloadEntityClass) {
        return entityManager.find(payloadEntityClass, recordId);
    }

    public <P extends AbstractPayloadEntity> void remove(Long recordId, Class<P> payloadEntityClass) {
        P payload = entityManager.find(payloadEntityClass, recordId);
        if (payload != null) {
            entityManager.remove(payload);
        }
    }

    public List<AbstractPayloadEntity> list(DatasetEntity dataset) {
        TypedQuery<AbstractPayloadEntity> query = entityManager
                .createNamedQuery("AbstractPayloadEntity.forDataset", AbstractPayloadEntity.class);
        query.setParameter("dataset", dataset);
        return query.getResultList();
    }

    public void removeAll(DatasetEntity dataset) {
        list(dataset).forEach(p -> entityManager.remove(p));
    }
}
