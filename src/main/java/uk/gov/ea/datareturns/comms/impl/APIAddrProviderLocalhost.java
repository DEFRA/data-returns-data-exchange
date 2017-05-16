package uk.gov.ea.datareturns.comms.impl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import uk.gov.ea.datareturns.comms.APIAddrProvider;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.HashSet;
import java.util.Set;

/**
 * @author Graham Willis
 * Provide a temporary (development) list of addresses. Please set up /etc/hosts to provide two valid
 * api endpoints for the names 'Api01', 'Api02' and 'Api03'
 */
public class APIAddrProviderLocalhost implements APIAddrProvider {
    private static final Logger LOGGER = LoggerFactory.getLogger(APIAddrProviderLocalhost.class);

    @Override
    public Set<InetAddress> getAddresses() {
        try {
            Set<InetAddress> addresses = new HashSet<>();
            InetAddress local = InetAddress.getLocalHost();
            addresses.add(local);

            return addresses;
        } catch (UnknownHostException e) {
            LOGGER.error("Cannot resolve hostname: " + e.getMessage());
            return null;
        }
    }
}
