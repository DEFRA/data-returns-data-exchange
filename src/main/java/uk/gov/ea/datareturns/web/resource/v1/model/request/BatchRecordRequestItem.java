package uk.gov.ea.datareturns.web.resource.v1.model.request;

import io.swagger.annotations.ApiModelProperty;
import uk.gov.ea.datareturns.web.resource.v1.model.record.payload.Payload;
import uk.gov.ea.datareturns.web.resource.v1.model.common.Preconditions;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * Individual request item used in a batch record request
 *
 * @author Sam Gardner-Dell
 */
@XmlRootElement(name = "request")
public class BatchRecordRequestItem {
    @XmlElement(name = "record_id")
    @ApiModelProperty(name = "record_id", notes = "The target `record_id`")
    private String recordId;

    @XmlElement(name = "preconditions")
    @ApiModelProperty(name = "preconditions", notes = "Support for RFC7232 conditional requests based on last modification time")
    private Preconditions preconditions;

    @ApiModelProperty(notes = "The payload to be associated with the given `record_id`", required = true)
    private Payload payload;

    public BatchRecordRequestItem() {

    }

    public String getRecordId() {
        return recordId;
    }

    public void setRecordId(String recordId) {
        this.recordId = recordId;
    }

    public Preconditions getPreconditions() {
        return preconditions;
    }

    public void setPreconditions(Preconditions preconditions) {
        this.preconditions = preconditions;
    }

    public Payload getPayload() {
        return payload;
    }

    public void setPayload(Payload payload) {
        this.payload = payload;
    }
}
