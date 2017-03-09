package uk.gov.ea.datareturns.comms;

import java.net.InetAddress;
import java.util.Set;

/**
 * @author Graham Willis
 * Provide a list addresses representing the API instances within the cluster
 */
public interface APIAddrProvider {
    Set<InetAddress> getAddresses();
}
