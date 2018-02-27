package uk.gov.defra.datareturns.data.projections.ewc;

import org.springframework.data.rest.core.config.Projection;
import uk.gov.defra.datareturns.data.model.ewc.EwcChapter;

import java.util.Set;

@Projection(name = "hierarchy", types = { EwcChapter.class })
@SuppressWarnings("unused")
public interface EwcChaptersIncludingSubchapters {

    Long getId();
    Set<EwcSubchaptersIncludingActivities> getEwcSubchapters();
}
