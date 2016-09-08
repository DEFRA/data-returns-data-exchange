package uk.gov.ea.datareturns.domain.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;

import java.time.LocalDate;
import java.util.List;

/**
 * Created by graham on 29/07/16.
 * Used as DTO to create the JSON response containing meta data about controlled lists
 */
public class ControlledListsDto {

    private final List<DisplayHeaderDto> displayHeaders;
    private String description;
    private String defaultSearch;
    private String path;

    @JsonIgnore
    private LocalDate lastUpdate;

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public ControlledListsDto(String description, String path, List<DisplayHeaderDto> displayHeaders, LocalDate lastUpdate,
            String defaultSearch) {
        this.description = description;
        this.path = path;
        this.lastUpdate = lastUpdate;
        this.displayHeaders = displayHeaders;
        this.defaultSearch = defaultSearch;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public LocalDate getLastUpdate() {
        return lastUpdate;
    }

    public void setLastUpdate(LocalDate lastUpdate) {
        this.lastUpdate = lastUpdate;
    }

    public List<DisplayHeaderDto> getDisplayHeaders() {
        return displayHeaders;
    }

    public String getDefaultSearch() {
        return defaultSearch;
    }

    public void setDefaultSearch(String defaultSearch) {
        this.defaultSearch = defaultSearch;
    }

}
