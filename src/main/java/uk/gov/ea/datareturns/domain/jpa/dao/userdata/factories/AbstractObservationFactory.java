package uk.gov.ea.datareturns.domain.jpa.dao.userdata.factories;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import uk.gov.ea.datareturns.domain.jpa.entities.userdata.AbstractObservation;
import uk.gov.ea.datareturns.web.resource.v1.model.record.payload.Payload;

import java.util.HashMap;
import java.util.Map;

/**
 * @author Graham Willis
 * Used so to keep the hibernate entities as clean representations
 * of the persistence objects
 */
public abstract class AbstractObservationFactory<M extends AbstractObservation, P extends Payload> {
    private static final Logger LOGGER = LoggerFactory.getLogger(AbstractObservationFactory.class);

    protected static Map<Class<?>, AbstractObservationFactory<?, ?>> factories = new HashMap<>();

    public AbstractObservationFactory(Class<P> payloadClass) {
        LOGGER.info("Adding factory for: " + payloadClass);
        factories.put(payloadClass, this);
    }

    public static <P extends Payload> AbstractObservationFactory factoryFor(Class<P> payloadClass) {
        return factories.get(payloadClass);
    }

    /**
     * Create the persistent entity from the DTO - data transfer object
     * @param payload
     * @return
     */
    public abstract M create(P payload);
}
