package uk.gov.defra.datareturns.data.projections.nose;

import org.springframework.data.rest.core.config.Projection;
import uk.gov.defra.datareturns.data.model.nosep.NoseProcess;

@Projection(name = "id", types = { NoseProcess.class })
@SuppressWarnings("unused")
public interface NoseProcessId {
    Long getId();
}
