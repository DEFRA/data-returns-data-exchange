package uk.gov.ea.datareturns.web.resource.v1.model.record.payload;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import io.swagger.annotations.ApiModel;

import javax.xml.bind.annotation.XmlRootElement;

/**
 * Base class for record data payload.
 *
 * Enables the API to support multiple data models for a record.
 *
 * @author Sam Gardner-Dell
 */
@XmlRootElement(name = "payload")
@ApiModel(
        description = "Payload base type",
        discriminator = "payload_type",
        subTypes = { DataSamplePayload.class, DemonstrationAlternativePayload.class }
)
@JsonTypeInfo(use = JsonTypeInfo.Id.NAME,
        include = JsonTypeInfo.As.PROPERTY,
        property = "payload_type"
)
@JsonSubTypes({
        @JsonSubTypes.Type(name = "DataSample", value = DataSamplePayload.class),
        @JsonSubTypes.Type(name = "Demo", value = DemonstrationAlternativePayload.class)
})

public abstract class Payload {
    public Payload() {
    }
}
