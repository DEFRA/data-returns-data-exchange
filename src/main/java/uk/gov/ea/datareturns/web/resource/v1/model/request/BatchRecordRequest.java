package uk.gov.ea.datareturns.web.resource.v1.model.request;

import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlElementWrapper;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;

import javax.xml.bind.annotation.XmlRootElement;
import java.util.List;

/**
 * Batch record request structure
 *
 * @author Sam Gardner-Dell
 */
@XmlRootElement(name = "batch")
public class BatchRecordRequest {
    @JacksonXmlElementWrapper(useWrapping = false)
    @JacksonXmlProperty(localName = "request")
    private List<BatchRecordRequestItem> requests;

    public BatchRecordRequest() {
    }

    public BatchRecordRequest(List<BatchRecordRequestItem> requests) {
        this.requests = requests;
    }

    public List<BatchRecordRequestItem> getRequests() {
        return requests;
    }

    public void setRequests(List<BatchRecordRequestItem> requests) {
        this.requests = requests;
    }

}
