package uk.gov.defra.datareturns.data.model.upload;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.querydsl.QueryDslPredicateExecutor;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;
import uk.gov.defra.datareturns.data.model.record.Upload;


/**
 * Spring REST repository for {@link Upload} entities
 *
 * @author Sam Gardner-Dell
 */
@RepositoryRestResource
public interface UploadRepository extends JpaRepository<Upload, Long>, JpaSpecificationExecutor<Upload>, QueryDslPredicateExecutor<Upload> {
}
