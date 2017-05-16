package uk.gov.ea.datareturns.comms.impl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import uk.gov.ea.datareturns.comms.InterAPICoordinator;
import uk.gov.ea.datareturns.comms.InterAPIServerListener;
import uk.gov.ea.datareturns.comms.InterAPIServer;
import uk.gov.ea.datareturns.comms.Message;

import java.io.BufferedInputStream;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.SocketException;

/**
 * Created by graham on 29/03/17.
 */
public class InterAPIUDPServer implements InterAPIServer {
    private static final Logger LOGGER = LoggerFactory.getLogger(InterAPIUDPServer.class);

    private static InterAPIUDPServer interAPIUDPServer = new InterAPIUDPServer();
    private InterAPIServerListener.QueueMessage addtoQueue;
    private DatagramSocket socket = null;

    private InterAPIUDPServer() {}

    public static InterAPIUDPServer instanceOf() {
        return interAPIUDPServer;
    }

    @Override
    public String getName() {
        return "UDP Server";
    }

    @Override
    public void listen() {
        LOGGER.info("Open listening socket on port: " + InterAPICoordinator.port);
        byte[] buf = new byte[Message.MAX_MESSAGE_SIZE];
        boolean listen = true;

        try {
            socket = new DatagramSocket(InterAPICoordinator.port);

            while (listen) {
                try {
                    DatagramPacket packet = new DatagramPacket(buf, buf.length);
                    socket.receive(packet);
                    ByteArrayInputStream byteStream = new ByteArrayInputStream(buf);

                    ObjectInputStream is = new ObjectInputStream(new BufferedInputStream(byteStream));
                    Message message = (Message) is.readObject();

                    LOGGER.debug("Message received: " + message.toString() + " size: " + packet.getLength());
                    addtoQueue.put(message);

                    is.close();
                } catch (IOException e ) {
                    LOGGER.error("IO error: " + e.getMessage());
                    listen = false;
                } catch (ClassNotFoundException e) {
                    LOGGER.error("Message deserialization error: " + e.getMessage());
                } catch (InterruptedException e) {
                    LOGGER.error("Cannot add message to message queue");
                    listen = false;
                }
            }
        } catch (SocketException e) {
            LOGGER.error("Socket error: " + e.getMessage());
        } finally {
            if (socket != null && !socket.isClosed()) {
                socket.close();
            }
        }
    }

    @Override
    public void setAddFunction(InterAPIServerListener.QueueMessage addtoQueue) {
        this.addtoQueue = addtoQueue;
    }
}
