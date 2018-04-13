package uk.gov.defra.datareturns.validation.service.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import lombok.Data;
import org.springframework.hateoas.Identifiable;
import org.springframework.hateoas.Link;
import org.springframework.hateoas.hal.Jackson2HalModule;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

@Data
public class MdBaseEntity implements Identifiable<Link>, Serializable {
    @JsonProperty("_links")
    @JsonSerialize(using = Jackson2HalModule.HalLinkListSerializer.class)
    @JsonDeserialize(using = Jackson2HalModule.HalLinkListDeserializer.class)
    private List<Link> links = new ArrayList<>();

    private String nomenclature;

    /**
     * Returns the link with the given rel.
     *
     * @param rel the rel value to find
     * @return the link with the given rel or {@literal null} if none found.
     */
    public Link getLink(final String rel) {
        for (final Link link : links) {
            if (link.getRel().equals(rel)) {
                return link;
            }
        }
        return null;
    }

    @Override
    public Link getId() {
        return getLink(Link.REL_SELF);
    }
}
