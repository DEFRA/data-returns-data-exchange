package uk.gov.ea.datareturns.domain.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import uk.gov.ea.datareturns.domain.jpa.entities.Site;
import uk.gov.ea.datareturns.domain.jpa.entities.UniqueIdentifier;

import java.util.Set;

/**
 * @Author Graham Willis
 * Data transfer class for search results
 */
public class PermitLookupDto {

    private String searchTerm;
    private String[] matches;

    @JsonProperty("results")
    private Set<Results> results;

    public static class Results {
        private final UniqueIdentifier uniqueIdentifier;
        private final Site site;
        private final Set<String> alternatives;

        public Results(UniqueIdentifier basePermit, Site site, Set<String> alternatives) {
            this.uniqueIdentifier = basePermit;
            this.site = site;
            this.alternatives = alternatives;
        }

        public UniqueIdentifier getUniqueIdentifier() {
            return uniqueIdentifier;
        }

        public Site getSite() {
            return site;
        }

        public Set<String> getAlternatives() {
            return alternatives;
        }
    }

    public PermitLookupDto(String searchTerm, String[] matches, Set<Results> results) {
        this.searchTerm = searchTerm;
        this.matches = matches;
        this.results = results;
    }

    public String getSearchTerm() {
        return searchTerm;
    }

    public void setSearchTerm(String searchTerm) {
        this.searchTerm = searchTerm;
    }

    public String[] getMatches() {
        return matches;
    }

    public void setMatches(String[] matches) {
        this.matches = matches;
    }

    public Set<Results> getResults() {
        return results;
    }

    public void setResults(Set<Results> results) {
        this.results = results;
    }
}
