package uk.gov.defra.datareturns.data.projections;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.rest.core.config.Projection;
import uk.gov.defra.datareturns.data.model.AliasedEntity;
import uk.gov.defra.datareturns.data.model.MasterDataEntity;

import java.util.Set;

@Projection(name = "inlineAlias", types = {AliasedEntity.class})
public interface BasicAliasData extends MasterDataEntity {
    String getNomenclature();

    @Value("#{target.aliases.![nomenclature]}")
    Set<String> getAliases();
}
