package uk.gov.defra.datareturns.data.projections;

import org.springframework.data.rest.core.config.Projection;
import uk.gov.defra.datareturns.data.model.ewc.EwcActivity;
import uk.gov.defra.datareturns.data.model.ewc.EwcChapter;
import uk.gov.defra.datareturns.data.model.ewc.EwcSubchapter;

import java.util.Set;

@org.springframework.data.rest.core.config.Projection(name = "inlineActivities", types = { EwcSubchapter.class })
@SuppressWarnings("unused")
public interface InlineEwcSubchapterProjection {
    Long getId();

    String getNomenclature();

    String getDescription();

    Set<EwcActivity> getEwcActivities();

}
