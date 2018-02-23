package uk.gov.defra.datareturns.data.projections.nace;


import org.springframework.data.rest.core.config.Projection;
import uk.gov.defra.datareturns.data.model.nace.NaceClass;

@Projection(name = "id", types = { NaceClass.class })
@SuppressWarnings("unused")
public interface NaceClassId {
    Long getId();
}
