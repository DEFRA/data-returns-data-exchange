package uk.gov.defra.datareturns.validation.service.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class MdParameter extends MdAliasableEntity<MdBaseEntity> {
    private String cas;
}
