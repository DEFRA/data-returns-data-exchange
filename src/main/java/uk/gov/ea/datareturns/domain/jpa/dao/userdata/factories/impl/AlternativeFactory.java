package uk.gov.ea.datareturns.domain.jpa.dao.userdata.factories.impl;

import uk.gov.ea.datareturns.domain.jpa.dao.userdata.factories.AbstractPayloadEntityFactory;
import uk.gov.ea.datareturns.domain.jpa.dao.userdata.factories.TranslationResult;
import uk.gov.ea.datareturns.domain.jpa.entities.userdata.impl.AlternativePayload;
import uk.gov.ea.datareturns.web.resource.v1.model.record.payload.DemonstrationAlternativePayload;

/**
 * @author Graham Willis
 * Used to generate instances of the hibernate persistence entity
 */
public class AlternativeFactory extends AbstractPayloadEntityFactory<AlternativePayload, DemonstrationAlternativePayload> {

    public AlternativeFactory() {
        super(DemonstrationAlternativePayload.class);
     }

    @Override
    public TranslationResult<AlternativePayload> create(DemonstrationAlternativePayload payload) {
        TranslationResult<AlternativePayload> result = new TranslationResult<>();
        AlternativePayload alternativePayload = new AlternativePayload();
        alternativePayload.setTest(payload.getTest());
        result.setEntity(alternativePayload);
        return result;
    }
}
