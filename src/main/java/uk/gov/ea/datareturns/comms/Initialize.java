package uk.gov.ea.datareturns.comms;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import uk.gov.ea.datareturns.comms.impl.APIAddrProviderLocalCluster;
import uk.gov.ea.datareturns.comms.impl.InterAPIUDPClient;
import uk.gov.ea.datareturns.comms.impl.InterAPIUDPServer;
import uk.gov.ea.datareturns.distributedtransaction.impl.DistributedTransactionServiceImpl;
import uk.gov.ea.datareturns.distributedtransaction.impl.RemoteCacheDelegate;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.inject.Inject;

/**
 * @author Graham Willis
 */
@Component
public class Initialize {

    @Inject
    private DistributedTransactionServiceImpl distributedTransactionService = null;

    @Inject
    private RemoteCacheDelegate remoteCacheDelegate = null;

    @PostConstruct
    public void initalizeComms() {
        // Get client request instance
        InterAPIClientRequester<InterAPIUDPClient> client =
                InterAPIClientRequester.getInstance(InterAPIUDPClient.instanceOf());

        // Get running server instance
        InterAPIServerListener<InterAPIUDPServer> server =
                InterAPIServerListener.getRunningInstance(InterAPIUDPServer.instanceOf());

        // Get message coordinator
        InterAPICoordinator coordinator =
                InterAPICoordinator.runningInstanceOf(client, server, distributedTransactionService, remoteCacheDelegate);

        // Initiate startup handshake negotiation
        coordinator.initiateNegotiation(new APIAddrProviderLocalCluster());
    }


    // Every 10 seconds poll all instances to check they are alive
    @Scheduled(fixedDelay = 10000, initialDelay = 10000)
    public void determineStillAlive() {
        InterAPICoordinator.runningInstanceOf().determineStillAlive();
    }

    // Clear up dead instances every minute
    @Scheduled(fixedDelay = 60 * 1000, initialDelay = 60 * 1000)
    public void resetDeadInstances() {
        InterAPICoordinator.runningInstanceOf().resetDeadInstances();
    }

    @PreDestroy
    public void terminateInstance() {
        InterAPICoordinator.runningInstanceOf().terminateInstance();
    }
}
