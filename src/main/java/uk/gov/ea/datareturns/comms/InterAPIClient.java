package uk.gov.ea.datareturns.comms;

import java.net.InetAddress;

/**
 * @author Graham Willis
 */
public interface InterAPIClient extends InterAPIActor {
    boolean send(Message message, InetAddress address);
}
