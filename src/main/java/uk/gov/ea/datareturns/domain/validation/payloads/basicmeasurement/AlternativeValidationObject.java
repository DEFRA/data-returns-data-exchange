package uk.gov.ea.datareturns.domain.validation.payloads.basicmeasurement;

import uk.gov.ea.datareturns.domain.validation.common.validator.AbstractValidationObject;
import uk.gov.ea.datareturns.web.resource.v1.model.record.payload.DemonstrationAlternativePayload;

import javax.validation.Valid;

/**
 * @author Graham Willis
 * Object contaioning entityfields and hibernate validation annotations
 */
public class AlternativeValidationObject extends AbstractValidationObject {

    @Valid private String test;

    /**
     * Initialize with a data transport object DTO
     *
     * @param payload
     */
    public AlternativeValidationObject(DemonstrationAlternativePayload payload) {
        super(payload);
        test = payload.getTest();
    }

    public String getTest() {
        return test;
    }

    public void setTest(String test) {
        this.test = test;
    }
}
