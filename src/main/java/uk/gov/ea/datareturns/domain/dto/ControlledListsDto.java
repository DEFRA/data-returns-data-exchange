package uk.gov.ea.datareturns.domain.dto;


import com.fasterxml.jackson.annotation.JsonIgnore;

import java.time.LocalDate;

/**
 * Created by graham on 29/07/16.
 * Used as DTO to create the JSON response containing meta data about controlled lists
 */
public class ControlledListsDto {

    private String description;

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    private String path;

    @JsonIgnore
    private LocalDate lastUpdate;

    public ControlledListsDto(String description, String path, LocalDate lastUpdate) {
        this.description = description;
        this.path = path;
        this.lastUpdate = lastUpdate;
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


}
