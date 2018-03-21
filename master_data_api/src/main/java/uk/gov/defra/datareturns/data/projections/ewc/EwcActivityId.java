package uk.gov.defra.datareturns.data.projections.ewc;

import org.springframework.data.rest.core.config.Projection;
import uk.gov.defra.datareturns.data.model.ewc.EwcActivity;

@Projection(name = "id", types = {EwcActivity.class})
@SuppressWarnings("unused")
public interface EwcActivityId {
    Long getId();
}
