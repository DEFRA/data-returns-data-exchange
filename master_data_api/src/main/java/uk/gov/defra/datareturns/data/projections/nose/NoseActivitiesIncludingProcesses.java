package uk.gov.defra.datareturns.data.projections.nose;

import org.springframework.data.rest.core.config.Projection;
import uk.gov.defra.datareturns.data.model.nosep.NoseActivity;

import java.util.Set;

@Projection(name = "hierarchy", types = { NoseActivity.class })
@SuppressWarnings("unused")
public interface NoseActivitiesIncludingProcesses {
    Long getId();
    Set<NoseProcessId> getNoseProcesses();
}
