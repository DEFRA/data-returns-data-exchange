package uk.gov.ea.datareturns.domain.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import uk.gov.ea.datareturns.domain.jpa.entities.UniqueIdentifier;

import java.util.Set;

/**
 * @Author Graham Willis
 * Data transfer class for search results
 */
public class PermitLookupDto {

    private String searchTerm;

    @JsonProperty("results")
    private Set<Results> results;

    public static class Results {
        private final UniqueIdentifier uniqueIdentifier;
        private final Set<String> alternatives;
        private final String[] matches;

        public Results(UniqueIdentifier basePermit, Set<String> alternatives, String[] matches) {
            this.uniqueIdentifier = basePermit;
            this.alternatives = alternatives;
            this.matches = matches;
        }

        public UniqueIdentifier getUniqueIdentifier() {
            return uniqueIdentifier;
        }

        public Set<String> getAlternatives() {
            return alternatives;
        }

        public String[] getMatches() {
            return matches;
        }
    }

    public PermitLookupDto(String searchTerm, Set<Results> results) {
        this.searchTerm = searchTerm;
        this.results = results;
    }

    public String getSearchTerm() {
        return searchTerm;
    }

    public void setSearchTerm(String searchTerm) {
        this.searchTerm = searchTerm;
    }

    public Set<Results> getResults() {
        return results;
    }

    public void setResults(Set<Results> results) {
        this.results = results;
    }
}
