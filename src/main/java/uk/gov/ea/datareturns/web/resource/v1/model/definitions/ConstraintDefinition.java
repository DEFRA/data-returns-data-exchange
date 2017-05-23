package uk.gov.ea.datareturns.web.resource.v1.model.definitions;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlElementWrapper;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import io.swagger.annotations.ApiModel;
import uk.gov.ea.datareturns.web.resource.v1.model.common.EntityBase;
import uk.gov.ea.datareturns.web.resource.v1.model.common.references.EntityReference;

import java.io.Serializable;
import java.util.Collection;

/**
 * Created by sam on 22/05/17.
 */
@ApiModel(parent = EntityBase.class)
public class ConstraintDefinition extends EntityBase implements Serializable {
    @JsonProperty(value = "description")
    private String description;

    @JsonProperty("fields")
    @JacksonXmlElementWrapper(localName = "fields")
    @JacksonXmlProperty(localName = "field")
    private Collection<EntityReference> fields;

    public ConstraintDefinition() {
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Collection<EntityReference> getFields() {
        return fields;
    }

    public void setFields(Collection<EntityReference> fields) {
        this.fields = fields;
    }
}
