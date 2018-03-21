package uk.gov.defra.datareturns.data.projections.nace;


import org.springframework.data.rest.core.config.Projection;
import uk.gov.defra.datareturns.data.model.nace.NaceDivision;

import java.util.Set;

@Projection(name = "hierarchy", types = {NaceDivision.class})
@SuppressWarnings("unused")
public interface NaceDivisionsIncludeGroups {
    Long getId();

    Set<NaceGroupsIncludeClasses> getNaceGroups();
}
