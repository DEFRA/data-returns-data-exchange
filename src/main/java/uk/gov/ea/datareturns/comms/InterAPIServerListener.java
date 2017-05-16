package uk.gov.ea.datareturns.comms;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

/**
 * @author Graham Willis
 */
public class InterAPIServerListener<T extends InterAPIServer> extends Thread {
    private static final Logger LOGGER = LoggerFactory.getLogger(InterAPIServerListener.class);
    private static volatile InterAPIServerListener interAPIInterfaceServerRunner = null;
    private T actor = null;

    // Holds the list of messages that the server has read and waiting to be processed
    private final static int QUEUE_LENGTH = 10000;
    private static final BlockingQueue<Message> inboundMessageQueue = new LinkedBlockingQueue<>(QUEUE_LENGTH);

    @FunctionalInterface
    public interface QueueMessage {
        void put(Message m) throws InterruptedException;
    }

    protected static QueueMessage addtoQueue = (m) -> inboundMessageQueue.put(m);

    /*
     * No instantiation allowed
     */
    private InterAPIServerListener() {
        // Give the server thread a name
        super("InterAPIServerListener");
    }

    public BlockingQueue<Message> getInboundMessageQueue() {
        return inboundMessageQueue;
    }

    /**
     * Returns the singleton running instance of the client communications thread
     * @return
     */
    public static <T extends InterAPIServer> InterAPIServerListener getRunningInstance(T actor) {
        if (interAPIInterfaceServerRunner == null) {
            synchronized (InterAPIServerListener.class) {
                if (interAPIInterfaceServerRunner == null) {
                    interAPIInterfaceServerRunner = new InterAPIServerListener();
                    interAPIInterfaceServerRunner.actor = actor;
                }
            }
        }

        if (interAPIInterfaceServerRunner.getState().equals(State.NEW)) {
            synchronized (InterAPIServerListener.class) {
                if (interAPIInterfaceServerRunner.getState().equals(State.NEW)) {
                    LOGGER.info("Starting communications thread with actor: " + actor.getName());

                    // Pass the function for the plug in to hand received requests to the queue
                    actor.setAddFunction(addtoQueue);
                    interAPIInterfaceServerRunner.start();
                }
            }
        }

        return interAPIInterfaceServerRunner;
    }

    /**
     * The running thread delegates the listening to the actor. It expects a blocking read.
     */
    public void run() {
        LOGGER.info(actor.getName() + " running...");

        // Run the delegate listener on actor
        actor.listen();
    }
}
