package uk.gov.ea.datareturns.domain.dto;

import uk.gov.ea.datareturns.domain.jpa.entities.ControlledListEntity;
import uk.gov.ea.datareturns.domain.jpa.entities.DependentEntity;

import java.util.List;

/**
 * Created by graham on 17/10/16.
 */
public class NavigationDto {
    private String name;
    private String description;
    private List<? extends ControlledListEntity> list;

    public NavigationDto(String name, String description, List<? extends ControlledListEntity> list) {
        this.name = name;
        this.description = description;
        this.list = list;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public List<? extends ControlledListEntity> getList() {
        return list;
    }

    public void setList(List<? extends DependentEntity> list) {
        this.list = list;
    }
}
