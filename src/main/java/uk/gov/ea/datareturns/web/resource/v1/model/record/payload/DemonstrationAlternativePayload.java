package uk.gov.ea.datareturns.web.resource.v1.model.record.payload;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.annotations.ApiModel;

/**
 * Demonstration payload - for demonstration of multiple payload type support only.  DO NOT USE.
 *
 * @author Sam Gardner-Dell
 */
@ApiModel(value = "DemonstrationAlternativePayload", description = "Demonstration payload", parent = Payload.class)
public final class DemonstrationAlternativePayload extends Payload {

    @JsonProperty("test")
    private String test;

    @JsonProperty("test_int")
    private int testInt;

    public String getTest() {
        return test;
    }

    public void setTest(String test) {
        this.test = test;
    }

    public int getTestInt() {
        return testInt;
    }

    public void setTestInt(int testInt) {
        this.testInt = testInt;
    }
}
