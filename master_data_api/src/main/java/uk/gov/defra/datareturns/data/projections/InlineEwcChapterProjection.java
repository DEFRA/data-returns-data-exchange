package uk.gov.defra.datareturns.data.projections;

import org.springframework.data.rest.core.config.Projection;
import uk.gov.defra.datareturns.data.model.eaid.UniqueIdentifier;
import uk.gov.defra.datareturns.data.model.ewc.EwcChapter;
import uk.gov.defra.datareturns.data.model.ewc.EwcSubchapter;
import uk.gov.defra.datareturns.data.model.site.Site;

import java.util.Set;

@Projection(name = "inlineChildren", types = { EwcChapter.class })
@SuppressWarnings("unused")
public interface InlineEwcChapterProjection {
    Long getId();

    String getNomenclature();

    String getDescription();

    Set<InlineEwcSubchapterProjection> getEwcSubchapters();

}
