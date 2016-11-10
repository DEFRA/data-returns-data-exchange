package uk.gov.ea.datareturns.domain.processors;

import org.springframework.stereotype.Component;
import uk.gov.ea.datareturns.domain.dto.PermitLookupDto;
import uk.gov.ea.datareturns.domain.jpa.dao.SiteDao;
import uk.gov.ea.datareturns.domain.jpa.entities.Site;
import uk.gov.ea.datareturns.domain.jpa.entities.UniqueIdentifier;
import uk.gov.ea.datareturns.domain.jpa.service.Search;
import uk.gov.ea.datareturns.domain.jpa.service.UniqueIdentifierService;

import javax.inject.Inject;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

/**
 * @Author Graham Willis
 */
@Component
public class SearchProcessor {

    private final SiteDao siteDao;
    private final Search search;
    private final UniqueIdentifierService uniqueIdentifierService;

    @Inject
    public SearchProcessor(Search search, UniqueIdentifierService uniqueIdentifierService, SiteDao siteDao) {
        this.search = search;
        this.uniqueIdentifierService = uniqueIdentifierService;
        this.siteDao = siteDao;
    }

    /**
     * Perform the site and permit lookup 
     * @param term
     * @return the data transfer object carrying the search results
     */
    public PermitLookupDto getBySiteOrPermit(String term) {
        // Look for an exact match on the permit. If found retrieve the site and
        // alternatives and populate and return the search dto object
        UniqueIdentifier basePermit = uniqueIdentifierService.getUniqueIdentifier(term.trim());
        if (basePermit == null) {
            // No permit number has not been found perform site search
            List<String> siteResults = search.searchSite(term);
            Set<PermitLookupDto.Results> results = new LinkedHashSet<>();
            for (String siteResult : siteResults) {
                // Lookup the site
                Site site = siteDao.getByName(siteResult);
                // From the site search results lookup the permit
                Set<UniqueIdentifier> siteBasePermits = uniqueIdentifierService.getUniqueIdentifierBySiteName(siteResult);
                // Loop through the multiple permits on the site
                for (UniqueIdentifier siteBasePermit : siteBasePermits) {
                    // Get the alternative permits
                    Set<String> alternatives = uniqueIdentifierService.getAliasNames(siteBasePermit);
                    results.add(new PermitLookupDto.Results(siteBasePermit, site, alternatives));
                }
            }
            return new PermitLookupDto(term, null, results);
        } else {
            // Get the set of alternatives using the base identifier
            Set<String> alternatives = uniqueIdentifierService.getAliasNames(basePermit);

            // Get the site
            Site site = uniqueIdentifierService.getSite(basePermit);
            //UniqueIdentifier basePermit, Site site, Set<String> alternatives
            PermitLookupDto.Results singleResult = new PermitLookupDto.Results(basePermit, site, alternatives);
            PermitLookupDto permitLookupDto = new PermitLookupDto(term, null, Collections.singleton(singleResult));
            return permitLookupDto;
        }
    }
}
