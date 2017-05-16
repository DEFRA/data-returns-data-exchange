package uk.gov.ea.datareturns.comms;

/**
 * @author Graham Willis
 */
public interface InterAPIServer extends InterAPIActor {
    void listen();
    void setAddFunction(InterAPIServerListener.QueueMessage addtoQueue);
}
