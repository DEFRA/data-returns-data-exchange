package uk.gov.defra.datareturns.data.projections.eaid;

import org.springframework.data.rest.core.config.Projection;
import uk.gov.defra.datareturns.data.model.eaid.UniqueIdentifier;
import uk.gov.defra.datareturns.data.model.site.Site;

@Projection(name = "inlineSites", types = {UniqueIdentifier.class})
@SuppressWarnings("unused")
public interface InlineUniqueIdentifierSitesProjection {
    Long getId();

    String getNomenclature();

    Site getSite();

}
