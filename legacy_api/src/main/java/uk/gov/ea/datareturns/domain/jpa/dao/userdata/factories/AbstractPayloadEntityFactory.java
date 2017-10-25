package uk.gov.ea.datareturns.domain.jpa.dao.userdata.factories;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import uk.gov.ea.datareturns.domain.jpa.entities.userdata.AbstractPayloadEntity;
import uk.gov.ea.datareturns.web.resource.v1.model.record.payload.Payload;

import java.util.HashMap;
import java.util.Map;

/**
 * @author Graham Willis
 * Factory for producing objects to create payload entity object from the payload object
 */
public abstract class AbstractPayloadEntityFactory<M extends AbstractPayloadEntity, P extends Payload> {
    private static final Logger LOGGER = LoggerFactory.getLogger(AbstractPayloadEntityFactory.class);

    protected static Map<Class<?>, AbstractPayloadEntityFactory<?, ?>> factories = new HashMap<>();

    public AbstractPayloadEntityFactory(Class<P> payloadClass) {
        LOGGER.info("Adding payload entity factory for: " + payloadClass);
        factories.put(payloadClass, this);
    }

    @SuppressWarnings("unchecked")
    public static <P extends Payload> AbstractPayloadEntityFactory<AbstractPayloadEntity, P> factoryFor(Class<P> payloadClass) {
        return (AbstractPayloadEntityFactory<AbstractPayloadEntity, P>) factories.get(payloadClass);
    }

    @SuppressWarnings("unchecked")
    public static AbstractPayloadEntityFactory<AbstractPayloadEntity, Payload> genericFactory(Class<? extends Payload> payloadClass) {
        return (AbstractPayloadEntityFactory<AbstractPayloadEntity, Payload>) factories.get(payloadClass);
    }

    /**
     * Create the persistent entity from the DTO - data transfer object
     * @param payload
     * @return
     */
    public abstract TranslationResult<M> create(P payload);

}
