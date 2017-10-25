package uk.gov.ea.datareturns.web.resource.v1.model.record;

import org.springframework.stereotype.Component;
import uk.gov.ea.datareturns.domain.jpa.entities.userdata.impl.RecordEntity;
import uk.gov.ea.datareturns.domain.jpa.service.SubmissionService;
import uk.gov.ea.datareturns.web.resource.v1.model.record.payload.Payload;

import javax.inject.Inject;
import java.util.Date;

/**
 * @author Graham Willis
 */
@Component
public class RecordAdaptor {

    private final SubmissionService submissionService;

    @Inject
    public RecordAdaptor(SubmissionService submissionService) {
        this.submissionService = submissionService;
    }

    public Record convert(RecordEntity recordEntity) {
        Record record = new Record();
        record.setId(recordEntity.getIdentifier());
        record.setCreated(Date.from(recordEntity.getCreateDate()));
        record.setLastModified(Date.from(recordEntity.getLastChangedDate()));
        Payload dataSamplePayload = submissionService.parseJsonObject(recordEntity.getJson());
        record.setPayload(dataSamplePayload);
        return record;
    }
}
