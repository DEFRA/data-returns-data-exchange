package uk.gov.defra.datareturns.data.model.nace;

import org.springframework.data.rest.core.annotation.RepositoryRestResource;


/**
 * Spring REST repository for {@link NaceGroup} entities
 *
 * @author Sam Gardner-Dell
 */
@RepositoryRestResource
public interface NaceGroupRepository extends NaceEntityRepository<NaceGroup> {
}
