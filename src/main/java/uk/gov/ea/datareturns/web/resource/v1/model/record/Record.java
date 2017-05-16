package uk.gov.ea.datareturns.web.resource.v1.model.record;

import io.swagger.annotations.ApiModel;
import uk.gov.ea.datareturns.web.resource.v1.model.record.payload.Payload;
import uk.gov.ea.datareturns.web.resource.v1.model.common.EntityBase;

/**
 * Record entity
 *
 * @author Sam Gardner-Dell
 */
@ApiModel
public final class Record extends EntityBase {
    private Payload payload;

    public Record() {
        super();
    }

    public Payload getPayload() {
        return payload;
    }

    public void setPayload(Payload payload) {
        this.payload = payload;
    }
}
