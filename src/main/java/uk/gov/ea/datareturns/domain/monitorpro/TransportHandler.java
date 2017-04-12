package uk.gov.ea.datareturns.domain.monitorpro;

import java.io.File;

/**
 * Handles the transport of data to the downstream system
 *
 * @author Sam Gardner-Dell
 */
public interface TransportHandler {
    /**
     * Send a notification to the downstream system for the given CSV file
     *
     * @param originatorEmail the email address of the user that uploaded the source file that this output file has been created from
     * @param originatorFilename the name of the file the user uploaded to the datareturns service
     * @param eaId the EA Unique Identifier that the data in the output file pertains to
     * @param returnsCSVFile the CSV file to send to MonitorPro
     * @throws MonitorProTransportException if a problem occurred when attempting to send the file to MonitorPro
     */
    void sendNotifications(String originatorEmail, String originatorFilename, String eaId, File returnsCSVFile)
            throws MonitorProTransportException;
}