package uk.gov.ea.datareturns.comms;

import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.Pair;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import uk.gov.ea.datareturns.comms.impl.CacheClearRequestPayload;
import uk.gov.ea.datareturns.comms.impl.InitiationPayload;
import uk.gov.ea.datareturns.comms.impl.LockRequestPayload;
import uk.gov.ea.datareturns.distributedtransaction.DistributedTransactionService;
import uk.gov.ea.datareturns.distributedtransaction.LockSubject;
import uk.gov.ea.datareturns.distributedtransaction.RemoteCache;
import uk.gov.ea.datareturns.distributedtransaction.impl.DistributedTransactionServiceImpl;

import java.io.IOException;
import java.net.InetAddress;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.ReentrantLock;

/**
 * @author Graham Willis
 * Singleton coordinator for API inter communication and responsible for
 * coordinating inter-api conversations. Runs a consumer that acts on messages
 * from the fifo message queue
 */
public class InterAPICoordinator extends Thread {
    private static final Logger LOGGER = LoggerFactory.getLogger(InterAPICoordinator.class);
    private static volatile InterAPICoordinator interAPICoordinator = null;

    public static int port = 40000;

    private final RemoteCache remoteCache;
    private DistributedTransactionService distributedTransactionService;
    private InterAPIClientRequester client;
    private InterAPIServerListener server;
    private InetAddress localhost = null;

    // TODO Move into configuration
    private static final int MILLISECONDS_DECLARE_DEAD = 5 * 60 * 1000;
    private final static long RESPONSE_TIMEOUT = 5;
    private final static TimeUnit RESPONSE_TIMEOUT_UNITS = TimeUnit.MINUTES;
    private final static ReentrantLock asyncMessageLock = new ReentrantLock();

    private static Map<Pair<InetAddress, Message>, Thread> asyncMessageStack =
            Collections.synchronizedMap(new HashMap<Pair<InetAddress, Message>, Thread>());

    private enum NegotiationStatus {
        UNINITIALIZED, CONFIRMED
    }

    @FunctionalInterface
    public interface RemoteLockRequest {
        boolean get(LockSubject locksubject);
    }

    /**
     * This tracks the status of all API instances within the cluster
     */
    private class APIInstanceTracker {
        private NegotiationStatus negotiationStatus;
        private LocalDateTime lastTimeHeardFrom;

        protected APIInstanceTracker() {
            this.negotiationStatus = NegotiationStatus.UNINITIALIZED;
        }

        public NegotiationStatus getNegotiationStatus() {
            return negotiationStatus;
        }

        public void setNegotiationStatus(NegotiationStatus negotiationStatus) {
            this.negotiationStatus = negotiationStatus;
            if (negotiationStatus == NegotiationStatus.CONFIRMED) {
                this.lastTimeHeardFrom = LocalDateTime.now();
            }
        }

        protected LocalDateTime getLastTimeHeardFrom() {
            return lastTimeHeardFrom;
        }
    }

    // Map the instance tracker entries by the network address
    private Map<InetAddress, APIInstanceTracker> instances = Collections.synchronizedMap(new HashMap<>());

    private InterAPICoordinator(InterAPIClientRequester client,
                                InterAPIServerListener server,
                                DistributedTransactionService distributedTransactionService,
                                RemoteCache remoteCache) {
        this.client = client;
        this.server = server;
        this.distributedTransactionService = distributedTransactionService;
        this.remoteCache = remoteCache;
        this.distributedTransactionService.remoteLockReleaseRequest(new RemoteLockReleaseRequest());
        this.distributedTransactionService.remoteLockAcquireRequest(new RemoteLockAcquireRequest());
    }

    /**
     * The static initializer for the singleton
     * @param client
     * @param server
     * @param remoteCache
     * @return the singleton instance
     */
    public static InterAPICoordinator runningInstanceOf(InterAPIClientRequester client, InterAPIServerListener server, DistributedTransactionService distributedTransactionService, RemoteCache remoteCache) {
        if (interAPICoordinator == null) {
            synchronized (InterAPICoordinator.class) {
                if (interAPICoordinator == null) {
                    interAPICoordinator = new InterAPICoordinator(client, server, distributedTransactionService, remoteCache);
                    interAPICoordinator.start();
                }
            }
        }
        return interAPICoordinator;
    }

    /**
     * Returns the singleton instance or null if not initialized
     * @return
     */
    public static InterAPICoordinator runningInstanceOf() {
        if (interAPICoordinator == null) {
            LOGGER.error("The coordinator is not initialized");
        }
        return interAPICoordinator;
    }

    /**
     * Sends a blocking message to all API instances.
     * Works round robin and expected a success for all remote nodes
     *
     * For each instance it takes the following steps
     *  (1) Acquire the lock or timeout
     *  (2) Log the current thread id to the asyncMessageStack for the message id and teh recipient
     *  (3) Send a client message
     *  (4) Wait.
     *
     *  The receipt of in the server will retrieve the thread identity from the asyncMessageStack
     *  and interrupt the thread, removing the thread from asyncMessageStack and releasing the lock
     *
     * @param asyncMessageLock
     * @param message
     */
    private boolean sendBlockingAsyncMessage(ReentrantLock asyncMessageLock, Message message) {
        boolean lockAcquired = false;
        boolean success = true;
        Thread currentThread = Thread.currentThread();
        for (InetAddress addr : instances.keySet()) {
            if (instances.get(addr).getNegotiationStatus() == NegotiationStatus.CONFIRMED) {
                Pair<InetAddress, Message> pair = new ImmutablePair<>(addr, message);
                try {
                    // The current (parent) thread attempts to acquire the lock
                    lockAcquired = asyncMessageLock.tryLock(RESPONSE_TIMEOUT, RESPONSE_TIMEOUT_UNITS);
                    if (lockAcquired) {
                        // If the lock is acquired then log the thread as locked
                        asyncMessageStack.put(pair, Thread.currentThread());
                        if (client.send(message, addr)) {
                            synchronized (currentThread) {
                                currentThread.wait();
                            }
                        } else {
                            success = false;
                        }
                    } else {
                        success = false;
                    }
                } catch (InterruptedException e) {

                } finally {
                    if (lockAcquired && asyncMessageLock.isHeldByCurrentThread()) {
                        asyncMessageLock.unlock();
                        if (asyncMessageStack.containsKey(pair)) {
                            asyncMessageStack.remove(pair);
                        }
                    }
                }
            }
        }
        return success;
    }

    /**
     * Request the set of remote locks and wait for the reentrant lock
     * to be released either by the synchronous client or by the server
     */
    public class RemoteLockAcquireRequest implements RemoteLockRequest {
        @Override
        public boolean get(LockSubject locksubject) {
            Message message = Message.createMessage(localhost, LockRequestPayload.acquireRequest(locksubject));

            // For each remote API send the request by starting an async thread
            return sendBlockingAsyncMessage(asyncMessageLock, message);
        }
    }

    /**
     * Request the release of remote locks and wait for the reentrant lock
     * to be released either by the synchronous client or by the server
     */
    public class RemoteLockReleaseRequest implements RemoteLockRequest {
        @Override
        public boolean get(LockSubject locksubject) {
            Message message = Message.createMessage(localhost, LockRequestPayload.releaseRequest(locksubject));
            return sendBlockingAsyncMessage(asyncMessageLock, message);
        }
    }

    /**
     * Request the set of cache clears and wait using an asynchronous
     * client message
     */
    public boolean clearRemoteCache(RemoteCache.Cache cache) {
        Message message = Message.createMessage(localhost, CacheClearRequestPayload.instanceOf(cache));
        return sendBlockingAsyncMessage(asyncMessageLock, message);
    }

    /**
     * The message queue consumer processes messages from the server input queue
     * @param message
     * @throws InterruptedException
     */
    private void processMessage(Message message) throws InterruptedException {
        APIInstanceTracker fromInstance = null;
        Message reply;

        // First check to see if the message is from a known address in the cluster
        LOGGER.debug("Processing message: " + message);

        if (!instances.containsKey(message.getFrom())) {
            LOGGER.error("Received message from an unknown address: " + message.getFrom());
            return;
        } else {
            fromInstance = instances.get(message.getFrom());
        }

        /**
         * This section
         */
        if (message.getPayload() instanceof InitiationPayload) {
            InitiationPayload p = (InitiationPayload)message.getPayload();
            switch (p.getType()) {
                case HELLO:
                    /*
                     * Set the instance status and send hello back
                     */
                    fromInstance.setNegotiationStatus(NegotiationStatus.CONFIRMED);
                    reply = Message.createReply(message, localhost, InitiationPayload.instanceOf(InitiationPayload.Type.HELLO_BACK));
                    client.send(reply, message.getFrom());
                    break;

                case HELLO_BACK:
                    /*
                     * Set the instance status
                     */
                    fromInstance.setNegotiationStatus(NegotiationStatus.CONFIRMED);
                    break;

                case GOODBYE:
                    /*
                     * Set the status back
                     */
                    fromInstance.setNegotiationStatus(NegotiationStatus.UNINITIALIZED);
                    break;

                default:
                    LOGGER.error("Received initiation payload from an unknown type: " + p.getType());
            }
        } else if (message.getPayload() instanceof CacheClearRequestPayload) {
            CacheClearRequestPayload cacheClearRequestPayload = (CacheClearRequestPayload)message.getPayload();
            if (cacheClearRequestPayload.getType() == CacheClearRequestPayload.Type.REQUESTED) {
                LOGGER.info("Cache clear request from remote: " + message.getFrom());
                remoteCache.clearCacheLocal(cacheClearRequestPayload.getCache());
                reply = Message.createReply(message, localhost, CacheClearRequestPayload.cleared(cacheClearRequestPayload));
                client.send(reply, message.getFrom());
            } else {
                // This is processing the reply from a remote cache clear so unblock
                LOGGER.info("Cache clear response from remote: " + message.getFrom());
                Pair<InetAddress, Message> pair = new ImmutablePair<>(message.getFrom(), message);
                Thread thread = asyncMessageStack.get(pair);
                if (thread != null) {
                    thread.interrupt();
                }
            }
        } else if (message.getPayload() instanceof LockRequestPayload) {
            LockRequestPayload lockRequestPayload = (LockRequestPayload)message.getPayload();
            // Determine if this is a request for a remote lock or a reply with the lock
            if (lockRequestPayload.getResult() == null) {
                // This is receiving a remote lock request
                LockSubject lockSubject = lockRequestPayload.getLockSubject();
                // We need to get the local singleton incarnation of the lock subject
                // rather than this one which is the result of de-serializing the message
                lockSubject = LockSubject.instanceOf(lockSubject.getSubject());
                // Either acquired or release the lock according to the request
                DistributedTransactionServiceImpl.DistributedTransactionLock distributedLock = distributedTransactionService.distributedTransactionLockFor(lockSubject);
                if (lockRequestPayload.getRequestType() == LockRequestPayload.RequestType.ACQUIRE) {
                    LOGGER.info("Lock acquire request from remote: " + message.getFrom());
                    if (distributedLock.acquire()) {
                        // Reply acquired
                        reply = Message.createReply(message, localhost, LockRequestPayload.acquired(lockRequestPayload));
                        client.send(reply, message.getFrom());
                    } else {
                        // Reply not acquired
                        reply = Message.createReply(message, localhost, LockRequestPayload.notAcquired(lockRequestPayload));
                        client.send(reply, message.getFrom());
                    }
                } else if (lockRequestPayload.getRequestType() == LockRequestPayload.RequestType.RELEASE) {
                    LOGGER.info("Lock release request from remote: " + message.getFrom());
                    distributedLock.release();
                    reply = Message.createReply(message, localhost, LockRequestPayload.released(lockRequestPayload));
                    client.send(reply, message.getFrom());
                }
            } else {
                // This is processing the reply from a remote lock request
                LOGGER.info("Lock acquire/release response from remote: " + message.getFrom());
                Pair<InetAddress, Message> pair = new ImmutablePair<>(message.getFrom(), message);
                Thread thread = asyncMessageStack.get(pair);
                if (thread != null) {
                    thread.interrupt();
                }
            }
        }
    }

    /**
     * Initiate a conversation
     * @param addressProvider
     */
    public void initiateNegotiation(APIAddrProvider addressProvider) {
        LOGGER.info("Starting API server handshake");
        try {

            localhost = InetAddress.getLocalHost();
            String localaddr = localhost.getHostAddress();

            // Initialise the instances collection with the API nodes
            for (InetAddress address : addressProvider.getAddresses()) {
                if (!address.isAnyLocalAddress() &&
                        !address.getHostAddress().equals(localaddr) &&
                        !address.getHostName().equals(localhost.getHostName()) ) {

                    // Adding address to the tracker
                    LOGGER.info("Adding API addresses to the tracker: " + address.getHostName() + "/" + address.getHostAddress());
                    instances.put(address, new APIInstanceTracker());
                }
            }

            // Create a hello message
            Message message = Message.createMessage(localhost, InitiationPayload.instanceOf(InitiationPayload.Type.HELLO));

            // Say hello to everybody
            for (InetAddress addr : instances.keySet()) {
                client.send(message, addr);
            }

        } catch (IOException e) {
            LOGGER.error("IO exception during negotiation" + e.getMessage());
        }
    }

    /**
     * The instance is shutting down gracefully
     */
    public void terminateInstance() {
        LOGGER.info("Starting API server handshake");
        // Create a hello message
        Message message = Message.createMessage(localhost, InitiationPayload.instanceOf(InitiationPayload.Type.GOODBYE));

        // Say goodbye to everybody
        for (InetAddress addr : instances.keySet()) {
            client.send(message, addr);
        }
    }

    /**
     * Send a new Hello (keep-alive message)
     */
    public void determineStillAlive() {
        // Just say hello to everybody
        Message message = Message.createMessage(localhost, InitiationPayload.instanceOf(InitiationPayload.Type.HELLO));
        for (InetAddress addr : instances.keySet()) {
            client.send(message, addr);
        }
    }

    /**
     * Anything not heard from for longer than the duration specified is set to dead
     */
    public void resetDeadInstances() {
        for (InetAddress addr : instances.keySet()) {
            APIInstanceTracker instance = instances.get(addr);
            if (instance.getNegotiationStatus() == NegotiationStatus.CONFIRMED) {
                LocalDateTime nowTime = LocalDateTime.now();
                long millisecondsElapsed = Duration.between(instance.getLastTimeHeardFrom(), nowTime).toMillis();
                if (millisecondsElapsed > MILLISECONDS_DECLARE_DEAD) {
                    instance.setNegotiationStatus(NegotiationStatus.UNINITIALIZED);
                    LOGGER.warn("Declared dead: " + addr + " after " + millisecondsElapsed + " milliseconds silence");
                }
            }
        }
    }

    /**
     * Thread runner calls queue consumer
     */
    public void run() {
        LOGGER.info("Starting server queue consumer thread process");
        BlockingQueue<Message> queue = server.getInboundMessageQueue();
        try {
            while (true) { processMessage(queue.take()); }
        } catch (InterruptedException e) {
            LOGGER.error("Queue consumer thread terminated: " + e);
        }
    }
 }
