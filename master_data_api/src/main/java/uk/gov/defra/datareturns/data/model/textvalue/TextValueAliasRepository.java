package uk.gov.defra.datareturns.data.model.textvalue;

import org.springframework.stereotype.Repository;
import uk.gov.defra.datareturns.data.model.MasterDataRepository;


/**
 * Spring REST repository for {@link TextValueAlias} entities
 *
 * @author Sam Gardner-Dell
 */
@Repository
public interface TextValueAliasRepository extends MasterDataRepository<TextValueAlias> {
}
