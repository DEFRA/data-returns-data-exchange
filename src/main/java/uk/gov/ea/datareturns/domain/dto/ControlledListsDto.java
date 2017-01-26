package uk.gov.ea.datareturns.domain.dto;

import java.util.List;
import java.util.Set;

/**
 * Created by graham on 29/07/16.
 * Used as DTO to create the JSON response containing meta data about controlled lists
 */
public class ControlledListsDto {

    private final List<DisplayHeaderDto> displayHeaders;
    private String description;
    private Set<String> searchFields;
    private String path;

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public ControlledListsDto(String description, String path, List<DisplayHeaderDto> displayHeaders,
            Set<String> searchFields) {
        this.description = description;
        this.path = path;
        this.displayHeaders = displayHeaders;
        this.searchFields = searchFields;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public List<DisplayHeaderDto> getDisplayHeaders() {
        return displayHeaders;
    }

    public Set<String> getSearchFields() {
        return searchFields;
    }

    public void setSearchFields(Set<String> searchFields) {
        this.searchFields = searchFields;
    }
}
