package uk.gov.defra.datareturns.data.projections;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.rest.core.config.Projection;
import uk.gov.defra.datareturns.data.model.ewc.EwcActivity;
import uk.gov.defra.datareturns.data.model.ewc.EwcSubchapter;

@Projection(name = "includeChapterId", types = { EwcSubchapter.class })
@SuppressWarnings("unused")
public interface EwcSubchaperIncludeChaperId {
    Long getId();

    @Value("#{target.id}")
    Long getEwcChapter();

    String getNomenclature();
    String getCode();
    String getDescription();
}
