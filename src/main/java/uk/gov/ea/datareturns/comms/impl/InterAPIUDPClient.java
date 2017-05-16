package uk.gov.ea.datareturns.comms.impl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import uk.gov.ea.datareturns.comms.InterAPIClient;
import uk.gov.ea.datareturns.comms.InterAPICoordinator;
import uk.gov.ea.datareturns.comms.Message;

import java.io.BufferedOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;

/**
 * Created by graham on 29/03/17.
 */
public class InterAPIUDPClient implements InterAPIClient {
    private static final Logger LOGGER = LoggerFactory.getLogger(InterAPIUDPClient.class);

    private static InterAPIUDPClient interAPIUDPClient = new InterAPIUDPClient();
    private InterAPIUDPClient() {}

    public static InterAPIUDPClient instanceOf() {
        return interAPIUDPClient;
    }

    @Override
    public String getName() {
        return "UDP Client";
    }

    @Override
    public boolean send(Message message, InetAddress address) {

        LOGGER.debug("Send message: " + message.toString() + " to " + address);

        ByteArrayOutputStream baoStream = new ByteArrayOutputStream(Message.MAX_MESSAGE_SIZE);
        DatagramSocket socket = null;
        ObjectOutputStream os = null;

        boolean sent = false;

        try {
            os = new ObjectOutputStream(new BufferedOutputStream(baoStream));
            os.flush();
            os.writeObject(message);
            os.flush();

            byte[] sendBuf = baoStream.toByteArray();
            socket = new DatagramSocket();
            DatagramPacket packet = new DatagramPacket(sendBuf, sendBuf.length, address, InterAPICoordinator.port);

            socket.send(packet);

            sent = true;
        } catch (IOException e) {
            LOGGER.error("Error sending message: " + e.getMessage());
        } finally {
            try {
                if (os != null) {
                    os.close();
                }
                if (socket != null && !socket.isClosed()) {
                    socket.close();
                }
            } catch (IOException e) {
                LOGGER.error("Failure to close socket or stream resource: " + e.getMessage());
            }
        }

        return sent;
    }
}
