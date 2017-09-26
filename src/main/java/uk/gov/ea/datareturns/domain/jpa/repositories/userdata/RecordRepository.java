package uk.gov.ea.datareturns.domain.jpa.repositories.userdata;

import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import uk.gov.ea.datareturns.domain.jpa.entities.userdata.impl.DatasetEntity;
import uk.gov.ea.datareturns.domain.jpa.entities.userdata.impl.RecordEntity;
import uk.gov.ea.datareturns.domain.jpa.repositories.BaseRepository;

import java.util.List;

/**
 * Repository for dataset records
 *
 * @author Sam Gardner-Dell
 */
@Repository
public interface RecordRepository extends BaseRepository<RecordEntity, Long> {

    /**
     * Get a record for the given dataset and record identifier
     *
     * @param dataset the target dataset
     * @param identifier the record identifier
     * @return the target {@link RecordEntity} or null if not found
     */
    RecordEntity getByDatasetAndIdentifier(DatasetEntity dataset, String identifier);

    /**
     * Remove a record for the given dataset and record identifier
     *
     * @param dataset the target dataset
     * @param identifier the record identifier
     */
    void removeByDatasetAndIdentifier(DatasetEntity dataset, String identifier);

    /**
     * Get a list of the records for a given dataset
     * @param dataset The dataset
     * @return a list of records
     */
    List<RecordEntity> findAllByDataset(DatasetEntity dataset);

    /**
     * Retrieve the validation errors for a dataset
     *
     * @param dataset the target dataset
     * @return the validation errors
     */
    @org.springframework.data.jpa.repository.Query(nativeQuery = true,
            value = "select r.identifier as recordIdentifier, r.payload_type as payloadType, e.error as constraintIdentifier" +
                    "  from ud_records r" +
                    "  join ud_record_validation_errors e" +
                    "    on r.id = e.record_id" +
                    "  join ud_datasets d" +
                    "    on r.dataset_id = d.id" +
                    " where d.id = :ds_id")
    // TODO: This is only necessary due to a bug with spring data projections and native queries, see https://jira.spring.io/browse/DATAJPA-980
    // Once this has been fixed, the getValidationErrorsForDataset could return a List<RecordRepository.ValidationErrorInstance> itself
    // Only change required is the method signature - the correct result column should be mapped automatically.
    List<Object[]> getValidationErrorsForDataset(@Param("ds_id") DatasetEntity dataset);

}