package uk.gov.ea.datareturns.web.resource.v1.model.common.references;

import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlElementWrapper;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

import java.util.ArrayList;
import java.util.List;

/**
 * Reference to an entity
 *
 * @author Sam Gardner-Dell
 */
@ApiModel(
        description = "Reference to an API entity such as a dataset or record."
)
public class EntityReference {
    private String id = null;

    @ApiModelProperty
    @JacksonXmlElementWrapper(localName = "links")
    @JacksonXmlProperty(localName = "link")
    private List<Link> links = new ArrayList<>();

    public EntityReference() {
    }

    public EntityReference(String id, String ref) {
        this.id = id;

        List<Link> refs = new ArrayList<>();
        Link self = new Link();
        self.setRel("self");
        self.setHref(ref);
        refs.add(self);

        this.links = refs;
    }

    public EntityReference(String id, List<Link> links) {
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

    public void setLinks(
            List<Link> links) {
        this.links = links;
    }
}
