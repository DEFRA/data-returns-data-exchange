package uk.gov.defra.datareturns.validation.service.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class MdUniqueIdentifier extends MdAliasableEntity<MdBaseEntity> {
    private MdSite site;
}
