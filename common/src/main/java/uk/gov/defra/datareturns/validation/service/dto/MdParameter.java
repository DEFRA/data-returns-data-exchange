package uk.gov.defra.datareturns.validation.service.dto;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
public class MdParameter extends MdBaseEntity {
    private String cas;
}
