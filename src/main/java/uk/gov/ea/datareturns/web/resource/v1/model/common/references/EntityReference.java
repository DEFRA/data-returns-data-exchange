package uk.gov.ea.datareturns.web.resource.v1.model.common.references;

import io.swagger.annotations.ApiModel;

import javax.xml.bind.annotation.XmlRootElement;

/**
 * Reference to an entity
 *
 * @author Sam Gardner-Dell
 */
@ApiModel(
        description = "Reference to an API entity such as a dataset or record."
)
@XmlRootElement(name = "reference")
public class EntityReference {
    private String id = null;

    private Link link = null;

    public EntityReference() {
    }

    public EntityReference(String id, String ref) {
        this.id = id;
        Link self = new Link();
        self.setRel("self");
        self.setHref(ref);
        this.link = self;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public Link getLink() {
        return link;
    }

    public void setLink(Link link) {
        this.link = link;
    }
}
