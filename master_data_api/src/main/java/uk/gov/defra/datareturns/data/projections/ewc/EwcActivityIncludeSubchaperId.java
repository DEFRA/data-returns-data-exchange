package uk.gov.defra.datareturns.data.projections.ewc;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.rest.core.config.Projection;
import uk.gov.defra.datareturns.data.model.ewc.EwcActivity;

@Projection(name = "includeSubchapterId", types = { EwcActivity.class })
@SuppressWarnings("unused")
public interface EwcActivityIncludeSubchaperId {
    Long getId();

    @Value("#{target.ewcSubchapter.id}")
    Long getEwcSubchapter();

    String getNomenclature();
    String getCode();
    String getDescription();
    boolean getHazardous();
}
