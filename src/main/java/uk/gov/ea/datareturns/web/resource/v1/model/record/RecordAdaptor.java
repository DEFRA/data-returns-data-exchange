package uk.gov.ea.datareturns.web.resource.v1.model.record;

import org.springframework.stereotype.Component;
import uk.gov.ea.datareturns.config.SubmissionConfiguration;
import uk.gov.ea.datareturns.domain.jpa.entities.userdata.impl.RecordEntity;
import uk.gov.ea.datareturns.domain.jpa.service.SubmissionService;
import uk.gov.ea.datareturns.web.resource.v1.model.common.EntityAdaptor;
import uk.gov.ea.datareturns.web.resource.v1.model.record.payload.DataSamplePayload;

import javax.annotation.Resource;
import java.util.Date;
import java.util.Map;

/**
 * @author Graham Willis
 */
@Component
public class RecordAdaptor implements EntityAdaptor<Record, RecordEntity> {

    private SubmissionService<DataSamplePayload, ?, ?> submissionService;

    /**
     * Retrieves the appropriate versioned submission service
     * @param submissionServiceMap
     */
    @Resource(name = "submissionServiceMap")
    private void setSubmissionService(Map<SubmissionConfiguration.SubmissionServiceProvider, SubmissionService> submissionServiceMap) {
        this.submissionService = submissionServiceMap.get(SubmissionConfiguration.SubmissionServiceProvider.DATA_SAMPLE_V1);
    }

    @Override
    public RecordEntity convert(Record record) {
        return null;
    }

    @Override
    public Record convert(RecordEntity recordEntity) {
        Record record = new Record();
        record.setId(recordEntity.getIdentifier());
        record.setCreated(Date.from(recordEntity.getCreateDate()));
        record.setLastModified(Date.from(recordEntity.getLastChangedDate()));
        DataSamplePayload dataSamplePayload = submissionService.parseJsonObject(recordEntity.getJson());
        record.setPayload(dataSamplePayload);
        return record;
    }

    @Override
    public RecordEntity merge(RecordEntity recordEntity, Record record) {
        return null;
    }
}
