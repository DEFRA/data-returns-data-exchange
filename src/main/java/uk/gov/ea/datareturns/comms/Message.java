package uk.gov.ea.datareturns.comms;

import java.io.Serializable;
import java.net.InetAddress;
import java.time.LocalDateTime;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.atomic.AtomicLong;

/**
 * @author Graham Willis
 * Message class - single interprocess message content.
 */
public class Message<P extends Payload> implements Serializable {
    private final InetAddress from;
    private final P payload;
    private final LocalDateTime createTime;
    public final static int MAX_MESSAGE_SIZE = 5000;

    // Use this to set an arbitrary start point for the Id. This is not used algorithmically
    // but makes debugging easier.
    private static final int r = ThreadLocalRandom.current().nextInt(0, 4);
    static final AtomicLong atomicLong = new AtomicLong(r * 10000000);

    // Unique identifier for the message. The remote server should copy the identifier into
    // the message response so that the request and the response can be tied together.
    private final long uniqueIdentifier;

    static long getMessageId() {
        long l = atomicLong.getAndIncrement();
        if (l == Long.MAX_VALUE) {
            atomicLong.set(r);
        }
        return l;
    }

    private Message(InetAddress from, P payload) {
        this.from = from;
        this.payload = payload;
        this.createTime = LocalDateTime.now();
        this.uniqueIdentifier = Message.getMessageId();
    }

    private Message(InetAddress from, P payload, long id) {
        this.from = from;
        this.payload = payload;
        this.createTime = LocalDateTime.now();
        this.uniqueIdentifier = id;
    }

    public static <P extends Payload> Message createMessage(InetAddress from, P payload) {
        return new Message(from, payload);
    }

    public static <P extends Payload> Message createReply(Message message, InetAddress from, P payload) {
        return new Message(from, payload, message.uniqueIdentifier);
    }


    public InetAddress getFrom() {
        return from;
    }
    public P getPayload() { return payload; };

    @Override
    public String toString() {
        return "Message{" +
                "from=" + from +
                ", payload=" + payload +
                ", uniqueIdentifier=" + uniqueIdentifier +
                ", createTime=" + createTime +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Message<?> message = (Message<?>) o;

        return uniqueIdentifier == message.uniqueIdentifier;
    }

    @Override
    public int hashCode() {
        return (int) (uniqueIdentifier ^ (uniqueIdentifier >>> 32));
    }

    public long getUniqueIdentifier() {
        return uniqueIdentifier;
    }
}
