package uk.gov.defra.datareturns.data.projections.nose;

import org.springframework.data.rest.core.config.Projection;
import uk.gov.defra.datareturns.data.model.nosep.NoseActivityClass;

import java.util.Set;

@Projection(name = "hierarchy", types = {NoseActivityClass.class})
@SuppressWarnings("unused")
public interface NoseActivityClassesIncludingClasses {
    Long getId();

    Set<NoseActivitiesIncludingProcesses> getNoseActivities();
}
