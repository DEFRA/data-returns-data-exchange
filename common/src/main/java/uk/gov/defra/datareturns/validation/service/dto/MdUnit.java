package uk.gov.defra.datareturns.validation.service.dto;

import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
public class MdUnit extends MdAliasableEntity<MdBaseEntity> {
    private BigDecimal conversion;
}
