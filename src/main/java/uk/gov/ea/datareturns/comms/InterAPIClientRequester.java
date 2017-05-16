package uk.gov.ea.datareturns.comms;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.InetAddress;

/**
 * Created by graham on 29/03/17.
 */
public class InterAPIClientRequester<T extends InterAPIClient> {
    private static final Logger LOGGER = LoggerFactory.getLogger(InterAPIClientRequester.class);
    private static volatile InterAPIClientRequester interAPIInterfaceClientRunner = null;
    private T actor = null;

    /*
     * No instantiation allowed
     */
    private InterAPIClientRequester() {}

    /**
     * Returns the singleton running instance of the client requestor
     * @param actor the client plug-in
     * @return
     */
    public static <T extends InterAPIClient> InterAPIClientRequester getInstance(T actor) {
        if (interAPIInterfaceClientRunner == null) {
            synchronized (InterAPIClientRequester.class) {
                if (interAPIInterfaceClientRunner == null) {
                    interAPIInterfaceClientRunner = new InterAPIClientRequester();
                    interAPIInterfaceClientRunner.actor = actor;
                }
            }
        }

        return interAPIInterfaceClientRunner;
    }

    public boolean send(Message message, InetAddress address) {
        // Delegate to the transport
        return actor.send(message, address);
    }
}
