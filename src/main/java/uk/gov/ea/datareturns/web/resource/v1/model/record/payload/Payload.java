package uk.gov.ea.datareturns.web.resource.v1.model.record.payload;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import io.swagger.annotations.ApiModel;

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
public abstract class Payload implements Serializable {
    public static final Map<String, Class<?>> TYPES = Arrays.stream(Payload.class.getAnnotation(JsonSubTypes.class).value())
            .collect(Collectors.toMap(JsonSubTypes.Type::name, JsonSubTypes.Type::value));

    // Get a reverse map (we have to have a 1-1 here)
    public static final Map<Class<?>, String> NAMES = TYPES.entrySet().stream().collect(Collectors.toMap(t -> t.getValue(), t -> t.getKey()));

    public Payload() {
    }
}
