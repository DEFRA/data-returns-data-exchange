package uk.gov.ea.datareturns.web.resource.v1.model.common.references;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlElementWrapper;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import io.swagger.annotations.ApiModel;

import javax.xml.bind.annotation.XmlRootElement;
import java.io.Serializable;
import java.util.List;

/**
 * Reference to an entity
 *
 * @author Sam Gardner-Dell
 */
@ApiModel(
        description = "Reference to an API entity such as a dataset or record."
)
@XmlRootElement(name = "payload")
public class PayloadReference implements Serializable {
    @JsonProperty("payload_type")
    private String id = null;

    @JacksonXmlElementWrapper(localName = "links")
    @JacksonXmlProperty(localName = "link")
    private List<Link> links;

    public PayloadReference() {
    }

    public PayloadReference(String id) {
        this.id = id;
    }

    public PayloadReference(String id, List<Link> links) {
        this.id = id;
        this.links = links;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public List<Link> getLinks() {
        return links;
    }

    public void setLinks(List<Link> links) {
        this.links = links;
    }
}
