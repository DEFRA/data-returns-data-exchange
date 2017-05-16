package uk.gov.ea.datareturns.web.resource.v1.model.dataset;

import io.swagger.annotations.ApiModel;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import java.io.Serializable;

/**
 * User-defined fields to be associated with a dataset.  All fields within this model are user assignable.
 *
 * @author Sam Gardner-Dell
 */
@ApiModel(description = "User defined fields for a dataset")
@XmlRootElement(name = "properties")
public class DatasetProperties implements Serializable {

    @XmlElement(name = "originator_email")
    private String originatorEmail;

    public DatasetProperties() {
    }

    public String getOriginatorEmail() {
        return originatorEmail;
    }

    public void setOriginatorEmail(String originatorEmail) {
        this.originatorEmail = originatorEmail;
    }
}