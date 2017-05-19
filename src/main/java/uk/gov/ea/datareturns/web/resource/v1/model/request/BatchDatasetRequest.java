package uk.gov.ea.datareturns.web.resource.v1.model.request;

import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlElementWrapper;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;

import javax.xml.bind.annotation.XmlRootElement;
import java.util.List;

/**
 * Batch dataset request structure
 *
 * @author Sam Gardner-Dell
 */
@XmlRootElement(name = "batch")
public class BatchDatasetRequest {
    @JacksonXmlElementWrapper(useWrapping = false)
    @JacksonXmlProperty(localName = "request")
    private List<BatchDatasetRequestItem> requests;

    public BatchDatasetRequest() {
    }

    public BatchDatasetRequest(List<BatchDatasetRequestItem> requests) {
        this.requests = requests;
    }

    public List<BatchDatasetRequestItem> getRequests() {
        return requests;
    }

    public void setRequests(List<BatchDatasetRequestItem> requests) {
        this.requests = requests;
    }

}
