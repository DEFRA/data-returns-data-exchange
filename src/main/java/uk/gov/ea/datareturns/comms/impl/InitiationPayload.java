package uk.gov.ea.datareturns.comms.impl;

import uk.gov.ea.datareturns.comms.Payload;

import java.io.Serializable;

/**
 * Created by graham on 03/04/17.
 */
public class InitiationPayload implements Payload, Serializable {
    public enum Type { GOODBYE, HELLO, HELLO_BACK }
    private Type type;

    private InitiationPayload(Type type) {
        this.type = type;
    }

    public static InitiationPayload instanceOf(Type type) {
        return new InitiationPayload(type);
    }

    public Type getType() {
        return type;
    }

    @Override
    public String toString() {
        return "InitiationPayload{" +
                "type=" + type +
                '}';
    }
}
