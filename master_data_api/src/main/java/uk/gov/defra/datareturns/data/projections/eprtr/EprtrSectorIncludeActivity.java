package uk.gov.defra.datareturns.data.projections.eprtr;

import org.springframework.data.rest.core.config.Projection;
import uk.gov.defra.datareturns.data.model.eprtr.EprtrSector;

import java.util.Set;

@Projection(name = "hierarchy", types = {EprtrSector.class})
@SuppressWarnings("unused")
public interface EprtrSectorIncludeActivity {
    Long getId();

    Set<EprtrActivityId> getEprtrActivities();
}
