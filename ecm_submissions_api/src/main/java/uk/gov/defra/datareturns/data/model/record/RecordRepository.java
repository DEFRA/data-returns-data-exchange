package uk.gov.defra.datareturns.data.model.record;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.querydsl.QueryDslPredicateExecutor;
import org.springframework.data.repository.query.Param;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;
import uk.gov.defra.datareturns.data.model.dataset.Dataset;

import java.util.List;


/**
 * Spring REST repository for {@link Record} entities
 *
 * @author Sam Gardner-Dell
 */
@RepositoryRestResource
public interface RecordRepository extends JpaRepository<Record, Long>, JpaSpecificationExecutor<Record>, QueryDslPredicateExecutor<Record> {
    /**
     * Get a list of the records for a given dataset
     *
     * @param dataset The dataset
     * @return a list of records
     */
    List<Record> findAllByDataset(@Param("dataset") Dataset dataset);
}
