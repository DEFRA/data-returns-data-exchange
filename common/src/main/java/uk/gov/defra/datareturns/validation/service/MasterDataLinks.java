package uk.gov.defra.datareturns.validation.service;

import org.springframework.hateoas.Link;
import org.springframework.hateoas.UriTemplate;
import uk.gov.defra.datareturns.data.Context;

/**
 * The {@link MasterDataLinks} class provides a set of useful functions to construct links to retrieve data from the master data API
 *
 * @author Sam Gardner-Dell
 */
public final class MasterDataLinks {

    /**
     * UriTemplate to fetch the list of regimes for a given reporting reference and context
     */
    private static final UriTemplate TEMPLATE_REGIME_FOR_CONTEXT_AND_UNIQUE_IDENTIFIER =
            new UriTemplate("regimes/search/findRegimesForContextAndUniqueIdentifier{?context,id}");


    /**
     * Private constructor
     */
    private MasterDataLinks() {
    }

    /**
     * Retrieve a {@link Link} which may be used to retrieve one or more {@link uk.gov.defra.datareturns.validation.service.dto.MdRegime}s
     * for a given context and reporting reference.
     *
     * @param context            the reporting context (e.g. PI, ECM)
     * @param reportingReference the reporting reference (e.g. permit, reporting reference number
     * @return a {@link Link} which may be used to retrieve one or more regimes from the master data API.
     */
    public static Link findRegimesForContextAndUniqueIdentifier(final Context context, final String reportingReference) {
        return new Link(TEMPLATE_REGIME_FOR_CONTEXT_AND_UNIQUE_IDENTIFIER, "regime").expand(context, "uniqueIdentifiers/" + reportingReference);
    }
}
