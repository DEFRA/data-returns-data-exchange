package uk.gov.defra.datareturns.data.projections.nace;


import org.springframework.data.rest.core.config.Projection;
import uk.gov.defra.datareturns.data.model.nace.NaceSection;

import java.util.Set;

@Projection(name = "hierarchy", types = {NaceSection.class})
@SuppressWarnings("unused")
public interface NaceSectionsIncludeDivisions {
    Long getId();

    Set<NaceDivisionsIncludeGroups> getNaceDivisions();
}
