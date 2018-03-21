package uk.gov.defra.datareturns.data.projections.eprtr;

import org.springframework.data.rest.core.config.Projection;
import uk.gov.defra.datareturns.data.model.eprtr.EprtrActivity;

@Projection(name = "id", types = {EprtrActivity.class})
@SuppressWarnings("unused")
public interface EprtrActivityId {
    Long getId();
}
