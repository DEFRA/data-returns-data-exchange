package uk.gov.ea.datareturns.web.resource.v1.model.record.payload;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlRootElement;
import java.io.Serializable;
import java.util.Arrays;
import java.util.Map;
import java.util.stream.Collectors;

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
        discriminator = "_payload_type",
        subTypes = { DataSamplePayload.class, DemonstrationAlternativePayload.class }
)
@JsonTypeInfo(use = JsonTypeInfo.Id.NAME,
        include = JsonTypeInfo.As.EXISTING_PROPERTY,
        property = "_payload_type",
        visible = true
)
@JsonSubTypes({
        @JsonSubTypes.Type(name = "DataSamplePayload", value = DataSamplePayload.class),
        @JsonSubTypes.Type(name = "DemonstrationAlternativePayload", value = DemonstrationAlternativePayload.class)
})
public abstract class Payload implements Serializable {
    public static final Map<String, Class<?>> TYPES = Arrays.stream(Payload.class.getAnnotation(JsonSubTypes.class).value())
            .collect(Collectors.toMap(JsonSubTypes.Type::name, JsonSubTypes.Type::value));

    @ApiModelProperty(name = "_payload_type", required = true, example = "DataSample")
    @JsonProperty("_payload_type")
    @XmlAttribute(name = "_payload_type")
    private String payloadType;

    // Get a reverse map (we have to have a 1-1 here)
    public static final Map<Class<?>, String> NAMES = TYPES.entrySet().stream()
            .collect(Collectors.toMap(t -> t.getValue(), t -> t.getKey()));

    public Payload() {
        payloadType = NAMES.get(this.getClass());
    }

    public String getPayloadType() {
        return payloadType;
    }

    public void setPayloadType(String payloadType) {
        this.payloadType = payloadType;
    }
}
