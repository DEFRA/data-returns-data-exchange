package uk.gov.ea.datareturns.web.resource.v1.model.eaid;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlElementWrapper;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import io.swagger.annotations.ApiModel;
import uk.gov.ea.datareturns.web.resource.v1.model.common.EntityBase;

import java.io.Serializable;
import java.util.Set;

@ApiModel(parent = EntityBase.class, description = "Permit or authorization")
public class EaId extends EntityBase implements Serializable {

    public EaId() {
        super();
    }

    @JsonProperty("site")
    private String siteName;

    @JacksonXmlElementWrapper(localName = "aliases")
    @JacksonXmlProperty(localName = "alias")
    private Set<String> aliases;

    @JsonProperty("type")
    private String identifierType;

    @JsonProperty("operator")
    private String operatorName;

    public String getSiteName() {
        return siteName;
    }

    public void setSiteName(String siteName) {
        this.siteName = siteName;
    }

    public Set<String> getAliases() {
        return aliases;
    }

    public void setAliases(Set<String> aliases) {
        this.aliases = aliases;
    }

    public String getIdentifierType() {
        return identifierType;
    }

    public void setIdentifierType(String identifierType) {
        this.identifierType = identifierType;
    }

    public String getOperatorName() {
        return operatorName;
    }

    public void setOperatorName(String operatorName) {
        this.operatorName = operatorName;
    }
}
