package uk.gov.ea.datareturns.domain.processors;

import org.apache.commons.lang3.tuple.Pair;
import org.springframework.stereotype.Component;
import uk.gov.ea.datareturns.domain.dto.impl.PermitLookupDto;
import uk.gov.ea.datareturns.domain.jpa.entities.masterdata.impl.UniqueIdentifier;
import uk.gov.ea.datareturns.domain.jpa.entities.masterdata.impl.UniqueIdentifierAlias;
import uk.gov.ea.datareturns.domain.jpa.repositories.masterdata.UniqueIdentifierAliasRepository;
import uk.gov.ea.datareturns.domain.jpa.repositories.masterdata.UniqueIdentifierRepository;
import uk.gov.ea.datareturns.domain.jpa.service.Search;

import javax.inject.Inject;
import java.util.*;
import java.util.stream.Collectors;

/**
 * @Author Graham Willis
 */
@Component
public class SearchProcessor {

    private final Search search;
    private final UniqueIdentifierRepository uniqueIdentifierRepository;
    private final UniqueIdentifierAliasRepository uniqueIdentifierAliasRepository;

    @Inject
    public SearchProcessor(Search search, UniqueIdentifierRepository uniqueIdentifierRepository,
            UniqueIdentifierAliasRepository uniqueIdentifierAliasRepository) {
        this.search = search;
        this.uniqueIdentifierRepository = uniqueIdentifierRepository;
        this.uniqueIdentifierAliasRepository = uniqueIdentifierAliasRepository;
    }

    /**
     * Perform the site and permit lookup 
     * @param term
     * @return the data transfer object carrying the search results
     */
    public PermitLookupDto getBySiteOrPermit(String term) {
        Set<PermitLookupDto.Results> results = new LinkedHashSet<>();
        // Look for an exact match on the permit. If found retrieve the
        // alternatives and populate and return the search dto object
        StringTokenizer tokenizer = new StringTokenizer(term, " ,.:;?!'");
        while (tokenizer.hasMoreTokens()) {
            String word = tokenizer.nextToken();

            UniqueIdentifier basePermit;
            UniqueIdentifierAlias alias = uniqueIdentifierAliasRepository.getByName(word);
            if (alias != null) {
                basePermit = alias.getPreferred();
            } else {
                basePermit = uniqueIdentifierRepository.getByName(word);
            }

            if (basePermit != null) {
                // Get the set of alternatives using the base identifier
                Set<String> alternatives = basePermit.getAliases().stream().map(UniqueIdentifierAlias::getName).collect(Collectors.toSet());
                // Check that the search result is not previously found
                if (!results.stream().map(PermitLookupDto.Results::getUniqueIdentifier)
                        .collect(Collectors.toSet()).contains(basePermit)) {
                    results.add(new PermitLookupDto.Results(basePermit, alternatives, new String[] { word }));
                }
            }
        }

        // If we have no results use lucene to try and find the site
        if (results.size() == 0) {
            tokenizer = new StringTokenizer(term, " ,.:;?!-()/");
            StringJoiner sj = new StringJoiner(" AND ");
            while (tokenizer.hasMoreTokens()) {
                String word = tokenizer.nextToken() + "*";
                sj.add(word);
            }
            List<Pair<String, String[]>> siteResults = search.searchSite(sj.toString());
            if (siteResults != null) {
                for (Pair<String, String[]> siteResult : siteResults) {
                    // From the site search results lookup the permit
                    List<UniqueIdentifier> siteBasePermits = uniqueIdentifierRepository
                            .findUniqueIdentifiersBySiteName(siteResult.getLeft());

                    // Loop through the multiple permits on the site
                    if (siteBasePermits != null) {
                        for (UniqueIdentifier siteBasePermit : siteBasePermits) {
                            // Check that the search result is not previously found
                            if (!results.stream().map(PermitLookupDto.Results::getUniqueIdentifier)
                                    .collect(Collectors.toSet()).contains(siteBasePermit)) {
                                // Get the alternative permits
                                Set<String> alternatives = siteBasePermit.getAliases().stream().map(UniqueIdentifierAlias::getName)
                                        .collect(Collectors.toSet());
                                results.add(new PermitLookupDto.Results(siteBasePermit, alternatives, siteResult.getRight()));
                            }
                        }
                    }
                }
            }
        }
        return new PermitLookupDto(term, results);
    }
}
