package uk.gov.defra.datareturns.data.model.dataset;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.querydsl.QueryDslPredicateExecutor;
import org.springframework.data.repository.query.Param;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

import java.util.List;


/**
 * Spring REST repository for {@link Dataset} entities
 *
 * @author Sam Gardner-Dell
 */
@RepositoryRestResource
public interface DatasetRepository extends JpaRepository<Dataset, Long>, JpaSpecificationExecutor<Dataset>, QueryDslPredicateExecutor<Dataset> {
    /**
     * Find all datasets for the given ea_id
     *
     * @param eaId the EA unique identifier to find relevant datasets
     * @return a {@link List} of all datasets for the given ea_id
     */
    List<Dataset> findByEaId(@Param("ea_id") String eaId);
}
