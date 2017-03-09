package uk.gov.ea.datareturns.events;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

/**
 * Created by graham on 08/03/17.
 */
@Component
public class EventHandlers {

    protected static final Logger LOGGER = LoggerFactory.getLogger(EventHandlers.class);

    public EventHandlers() {
        LOGGER.info("Hello event handler");
    }

    @TransactionalEventListener
    public void afterCommit(EntityCreatedEvent event) {
        LOGGER.info("Commit: " + event.getClass());
    }

    @TransactionalEventListener(phase = TransactionPhase.AFTER_ROLLBACK)
    public void afterRollback(EntityCreatedEvent event) {
        LOGGER.info("Rollback: " + event.getClass());
    }
}
