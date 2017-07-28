package uk.gov.ea.datareturns.domain.jpa.entities.userdata.impl;

import uk.gov.ea.datareturns.domain.jpa.entities.userdata.AbstractPayloadEntity;

import javax.persistence.Entity;
import javax.persistence.Table;

/**
 * @author Graham Willis
 */
@Entity
@Table(name = "alternatives")
public class AlternativePayload extends AbstractPayloadEntity {

    private String test;

    public String getTest() {
        return test;
    }

    public void setTest(String test) {
        this.test = test;
    }
}
