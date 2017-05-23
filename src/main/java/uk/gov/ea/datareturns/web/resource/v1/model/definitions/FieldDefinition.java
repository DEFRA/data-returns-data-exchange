package uk.gov.ea.datareturns.web.resource.v1.model.definitions;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlElementWrapper;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import io.swagger.annotations.ApiModel;
import uk.gov.ea.datareturns.domain.jpa.entities.masterdata.ControlledListEntity;
import uk.gov.ea.datareturns.web.resource.v1.model.common.EntityBase;

import java.io.Serializable;
import java.util.Collection;

/**
 * Created by sam on 22/05/17.
 */
@ApiModel(parent = EntityBase.class)
public class FieldDefinition extends EntityBase implements Serializable {
    @JsonProperty("description")
    private String description;

    @JsonProperty(value = "type")
    private String type;

    @JsonProperty("allowed_values")
    @JacksonXmlElementWrapper(localName = "allowed_values")
    @JacksonXmlProperty(localName = "allowed")
    private Collection<ControlledListEntity> allowed;

    public FieldDefinition() {
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public Collection<ControlledListEntity> getAllowed() {
        return allowed;
    }

    public void setAllowed(Collection<ControlledListEntity> allowed) {
        this.allowed = allowed;
    }
}
