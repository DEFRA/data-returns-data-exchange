package uk.gov.ea.datareturns.web.resource.v1.model.common.references;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlRootElement;
import java.util.Objects;

/**
 * HATEOAS link to a related resource
 *
 * @author Sam Gardner-Dell
 */
@ApiModel(description = "HATEOAS link to a related resource")
@XmlRootElement(name = "link")
public class Link {
    @XmlAttribute
    private String rel = null;
    @XmlAttribute
    private String href = null;

    public Link() {

    }

    public Link(String rel, String href) {
        this.rel = rel;
        this.href = href;
    }

    /**
     * The relationship to the target of the link
     **/
    public Link rel(String rel) {
        this.rel = rel;
        return this;
    }

    @ApiModelProperty(example = "self|dataset|record|status|definition", value = "The relationship to the target of the link")
    public String getRel() {
        return rel;
    }

    public void setRel(String rel) {
        this.rel = rel;
    }

    /**
     * The URL describing the linked resource
     **/
    public Link href(String href) {
        this.href = href;
        return this;
    }

    @ApiModelProperty(example = "/api/v1/....", value = "The URL describing the linked resource")
    public String getHref() {
        return href;
    }

    public void setHref(String href) {
        this.href = href;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        Link link = (Link) o;
        return Objects.equals(rel, link.rel) &&
                Objects.equals(href, link.href);
    }

    @Override
    public int hashCode() {
        return Objects.hash(rel, href);
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("class Link {\n");

        sb.append("    rel: ").append(toIndentedString(rel)).append("\n");
        sb.append("    href: ").append(toIndentedString(href)).append("\n");
        sb.append("}");
        return sb.toString();
    }

    /**
     * Convert the given object to string with each line indented by 4 spaces
     * (except the first line).
     */
    private String toIndentedString(Object o) {
        if (o == null) {
            return "null";
        }
        return o.toString().replace("\n", "\n    ");
    }
}
