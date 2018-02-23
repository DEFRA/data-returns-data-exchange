package uk.gov.defra.datareturns.data.projections.nace;

import org.springframework.data.rest.core.config.Projection;
import uk.gov.defra.datareturns.data.model.nace.NaceGroup;

import java.util.Set;

@Projection(name = "hierarchy", types = { NaceGroup.class })
@SuppressWarnings("unused")
public interface NaceGroupsIncludeClasses {
    Long getId();
    Set<NaceClassId> getNaceClasses();
}
