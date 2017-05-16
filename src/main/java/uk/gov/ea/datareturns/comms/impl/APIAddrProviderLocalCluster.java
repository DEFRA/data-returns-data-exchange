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
public class APIAddrProviderLocalCluster implements APIAddrProvider {
    private static final Logger LOGGER = LoggerFactory.getLogger(APIAddrProviderLocalCluster.class);

    @Override
    public Set<InetAddress> getAddresses() {
        try {
            Set<InetAddress> addresses = new HashSet<>();

            InetAddress api01 = InetAddress.getByName("api01");
            InetAddress api02 = InetAddress.getByName("api02");
            //InetAddress api03 = InetAddress.getByName("api03");
            InetAddress local = InetAddress.getLocalHost();

            addresses.add(api01);
            addresses.add(api02);
            //addresses.add(api03);
            addresses.add(local);

            return addresses;
        } catch (UnknownHostException e) {
            LOGGER.error("Cannot resolve hostname: " + e.getMessage());
            return null;
        }
    }
}
