package uk.gov.defra.datareturns.data.projections.ewc;

import org.springframework.data.rest.core.config.Projection;
import uk.gov.defra.datareturns.data.model.ewc.EwcSubchapter;

import java.util.Set;

@Projection(name = "hierarchy", types = {EwcSubchapter.class})
@SuppressWarnings("unused")
public interface EwcSubchaptersIncludingActivities {
    Long getId();

    Set<EwcActivityId> getEwcActivities();
}
